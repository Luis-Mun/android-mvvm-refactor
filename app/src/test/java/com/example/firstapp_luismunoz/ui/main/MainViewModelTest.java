package com.example.firstapp_luismunoz.ui.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.firstapp_luismunoz.fakes.FakeItemRepository;
import com.example.firstapp_luismunoz.model.Item;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class MainViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private FakeItemRepository repository;
    private MainViewModel viewModel;

    @Before
    public void setUp() {
        repository = new FakeItemRepository();
        viewModel = new MainViewModel(repository);
    }

    @Test
    public void addItem_validInput_savesTrimmedItemAndReportsSuccess() {
        viewModel.addItem("  Apples ", " 12 ");

        assertEquals(1, repository.snapshot().size());
        Item saved = repository.snapshot().get(0);
        assertEquals("Apples", saved.getName());
        assertEquals(12, saved.getQuantity());
        assertEquals("Item added", viewModel.getMessages().getValue().getContentIfNotHandled());
        assertNotNull(viewModel.getItemAdded().getValue().getContentIfNotHandled());
        assertFalse(viewModel.isBusy().getValue());
        assertNull(viewModel.getLowStockAlerts().getValue());
    }

    @Test
    public void addItem_blankName_reportsErrorAndSavesNothing() {
        viewModel.addItem("   ", "5");

        assertTrue(repository.snapshot().isEmpty());
        assertEquals("Item name is required", viewModel.getErrors().getValue().getContentIfNotHandled());
    }

    @Test
    public void addItem_nonNumericQuantity_reportsError() {
        viewModel.addItem("Apples", "abc");

        assertTrue(repository.snapshot().isEmpty());
        assertEquals("Quantity must be a whole number", viewModel.getErrors().getValue().getContentIfNotHandled());
    }

    @Test
    public void addItem_negativeQuantity_reportsError() {
        viewModel.addItem("Apples", "-1");

        assertTrue(repository.snapshot().isEmpty());
        assertEquals("Quantity cannot be negative", viewModel.getErrors().getValue().getContentIfNotHandled());
    }

    @Test
    public void addItem_zeroQuantity_requestsLowStockAlert() {
        viewModel.addItem("Apples", "0");

        assertEquals("Alert: Apples is out of stock!",
                viewModel.getLowStockAlerts().getValue().getContentIfNotHandled());
    }

    @Test
    public void addItem_repositoryFailure_reportsErrorAndClearsBusy() {
        repository.failNextCall = true;

        viewModel.addItem("Apples", "5");

        assertEquals("Could not add item", viewModel.getErrors().getValue().getContentIfNotHandled());
        assertFalse(viewModel.isBusy().getValue());
    }

    @Test
    public void adjustQuantity_increment_updatesStoredQuantity() {
        viewModel.addItem("Apples", "5");
        Item apples = repository.snapshot().get(0);

        viewModel.adjustQuantity(apples, +1);

        assertEquals(6, repository.snapshot().get(0).getQuantity());
    }

    @Test
    public void adjustQuantity_decrementToZero_requestsLowStockAlert() {
        viewModel.addItem("Apples", "1");
        Item apples = repository.snapshot().get(0);

        viewModel.adjustQuantity(apples, -1);

        assertEquals(0, repository.snapshot().get(0).getQuantity());
        assertEquals("Alert: Apples is out of stock!",
                viewModel.getLowStockAlerts().getValue().getContentIfNotHandled());
    }

    @Test
    public void adjustQuantity_alreadyZero_doesNothing() {
        viewModel.addItem("Apples", "0");
        Item apples = repository.snapshot().get(0);
        viewModel.getLowStockAlerts().getValue().getContentIfNotHandled(); // consume first alert

        viewModel.adjustQuantity(apples, -1);

        assertEquals(0, repository.snapshot().get(0).getQuantity());
        assertNull(viewModel.getLowStockAlerts().getValue().getContentIfNotHandled());
    }

    @Test
    public void adjustQuantity_repositoryFailure_reportsError() {
        viewModel.addItem("Apples", "5");
        Item apples = repository.snapshot().get(0);
        repository.failNextCall = true;

        viewModel.adjustQuantity(apples, +1);

        assertEquals("Could not update item", viewModel.getErrors().getValue().getContentIfNotHandled());
        assertEquals(5, repository.snapshot().get(0).getQuantity());
    }

    @Test
    public void deleteItem_removesItemAndReportsSuccess() {
        viewModel.addItem("Apples", "5");
        Item apples = repository.snapshot().get(0);

        viewModel.deleteItem(apples);

        assertTrue(repository.snapshot().isEmpty());
        assertEquals("Item deleted", viewModel.getMessages().getValue().getContentIfNotHandled());
    }

    @Test
    public void deleteItem_repositoryFailure_reportsError() {
        viewModel.addItem("Apples", "5");
        Item apples = repository.snapshot().get(0);
        repository.failNextCall = true;

        viewModel.deleteItem(apples);

        assertEquals(1, repository.snapshot().size());
        assertEquals("Could not delete item", viewModel.getErrors().getValue().getContentIfNotHandled());
    }

    @Test
    public void items_exposesRepositoryContents() {
        viewModel.addItem("Apples", "5");
        viewModel.addItem("Pears", "3");

        assertEquals(2, viewModel.getItems().getValue().size());
    }
}
