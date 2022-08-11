package com.techelevator.dao;

import com.techelevator.Exception.ItemNotFoundException;
import com.techelevator.model.Item;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcItemDao implements ItemDao{

    private final JdbcTemplate jdbcTemplate;

    public JdbcItemDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Item getItemByItemId(int itemId) {
        Item item = null;
        String sql = "SELECT * FROM items i JOIN lists_in_group lip ON lip.list_id = i.list_id " +
                "JOIN account_groups ag ON ag.group_id = lip.group_id;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, itemId);
        if(results.next()) {
            item = mapRowToItem(results);
        } else throw new ItemNotFoundException();
        return item;
    }

    @Override
    public List<Item> listAllItemsByListId(int listId, String username) {
        List<Item> itemLists = new ArrayList<>();
        String sql = "SELECT item_id, i.list_id,  item_name, quantity, date_added, created_by " +
                "FROM items i JOIN lists l ON  l.list_id = i.list_id JOIN accounts a " +
                "ON a.account_id = l.account_id JOIN users u ON u.user_id = a.user_id WHERE i.list_id = ? " +
                "AND username = ? ORDER BY item_name;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, listId, username);
        while(results.next()) {
            Item itemResult = mapRowToItem(results);
            itemLists.add(itemResult);
        }
        return itemLists;
    }

    @Override
    public Item createItem(Item item, String username) {

        String sql = "INSERT INTO items(item_name, list_id, quantity, date_added, created_by)" +
                "VALUES (?,?,?,current_date,?) RETURNING item_id;";
        Integer itemId = jdbcTemplate.queryForObject(sql,Integer.class, item.getItemName(), item.getListId(), item.getQuantity(), username);

        return getItemByItemId(itemId);
    }

    @Override
    public void removeItem(int itemId) {
        String sql = "DELETE FROM items WHERE item_id = ?;";
        jdbcTemplate.update(sql, itemId);
    }
    @Override
    public void updateItem(int itemId, String itemName, int quantity, String modifiedBy) {
        String sql = "UPDATE items SET item_name = ? , quantity = ?, date_modified = current_Date, modified_by = ?" +
                " WHERE item_id = ?; ";

        jdbcTemplate.update(sql, itemName, quantity, modifiedBy, itemId);
    }

    private Item mapRowToItem(SqlRowSet rowSet) {
        Item item = new Item();

        item.setItemId(rowSet.getInt("item_id"));
        item.setListId(rowSet.getInt("list_id"));
        item.setItemName(rowSet.getString("item_name"));
        item.setQuantity(rowSet.getInt("quantity"));
        item.setDateAdded(rowSet.getDate("date_added").toLocalDate());
        item.setCreatedBy(rowSet.getString("created_by"));
        item.setDateModified(rowSet.getDate("date_modified").toLocalDate());
        item.setModifiedBy(rowSet.getString("modified_By"));
        item.setGroupId(rowSet.getInt("group_id"));
        item.setMemberOfGroupId(rowSet.getInt("member_of_group_id"));

        return item;
    }
}
