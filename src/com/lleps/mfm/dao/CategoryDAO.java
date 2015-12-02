package com.lleps.mfm.dao;

import com.lleps.mfm.model.Category;
import com.lleps.mfm.model.Client;
import com.lleps.mfm.model.Payment;

import java.util.List;

/**
 * @author Leandro on 2/12/2015.
 */
public interface CategoryDAO {
    List<Category> loadAllCategories();

    void saveCategory(Category category);
    
    void saveCategoryPayments(Category category);
    void saveCategoryPayment(Category category, Payment payment);

    void saveCategoryClients(Category category);
    void saveCategoryClient(Category category, Client client);
}