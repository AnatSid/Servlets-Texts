package org.example.servletsHomework.servlet.TestTextServlets.MockClass;

import org.example.servletsHomework.dao.TextDao;
import org.example.servletsHomework.model.Text;

import java.util.ArrayList;
import java.util.List;

public class MockTextDao implements TextDao {

    private boolean addCalled = false;
    private boolean deleteCalled = false;
    private boolean deleteAllCalled = false;
    private boolean getByIdCalled = false;
    private boolean getAllCalled = false;

    private List<String> messages = new ArrayList<>();

    @Override
    public boolean add(Long userId, Text text) {
        addCalled = true;
        messages.add("addSuccess");
        return addCalled;
    }

    @Override
    public Text getTextById(Long userId, Long textId) {
        getByIdCalled = true;
        if (textId > 15) {
            return null;
        }
        return new Text(textId, "TestText",userId);
    }

    @Override
    public List<Text> getAllTexts(Long userId) {
        getAllCalled = true;
        return List.of(new Text(1L, "Test",userId));
    }

    @Override
    public void delete(Long userId, Long textId) {
        deleteCalled = true;
        messages.add("deleteSuccess");
    }

    @Override
    public void deleteAll(Long userId) {
        deleteAllCalled = true;
        messages.add("deleteAllSuccess");
    }

    public boolean isAddCalled() {
        return addCalled;
    }

    public boolean isDeleteCalled() {
        return deleteCalled;
    }

    public boolean isDeleteAllCalled() {
        return deleteAllCalled;
    }

    public boolean isGetByIdCalled() {
        return getByIdCalled;
    }

    public boolean isGetAllCalled() {
        return getAllCalled;
    }

    public String getMessage() {
        if(messages.isEmpty()){
            return null;
        }
        return messages.get(0);
    }

}