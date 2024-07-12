/*
 * The MIT License
 *
 * Copyright 2016 Maurice Amon.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.example.heimdallseye.Model.entities;

import com.example.heimdallseye.Controller.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maurice Amon
 */
public class UnitOfWorkDatabaseMapper {
    
    private final ArrayList<Entity> INSERT_OBJECT_LIST = new ArrayList<>();
    
    private final ArrayList<Entity> UPDATE_OBJECT_LIST = new ArrayList<>();
    
    private final ArrayList<Entity> DELETE_OBJECT_LIST = new ArrayList<>();
    
    private static UnitOfWorkDatabaseMapper uowMapper = null;
    
    private DatabaseConnection connection = DatabaseConnection.getInstance();
    
    private UnitOfWorkDatabaseMapper() {
        
    }
    
    public static UnitOfWorkDatabaseMapper getUnitOfWorkMapperInstance() {
        if(uowMapper == null) {
            uowMapper = new UnitOfWorkDatabaseMapper();
        }
        return uowMapper;
    }
    
    public void addObjectToInsertList(Entity entity) {
        INSERT_OBJECT_LIST.add(entity);
    }
    
    public void addObjectToUpdateList(Entity entity) {
        UPDATE_OBJECT_LIST.add(entity);
    }
    
    public void addObjectToDeleteList(Entity entity) {
        DELETE_OBJECT_LIST.add(entity);
    }
    
    public void rollback() {
        try {
            connection.getConnection().rollback();
        } catch (SQLException ex) {
            Logger.getLogger(UnitOfWorkDatabaseMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void commit() {
        try {
            // Initiate the transaction ...
            Connection con = connection.getConnection();
            con.setAutoCommit(false);
            INSERT_OBJECT_LIST.stream().forEach((entity) -> {
                entity.insert(con);
            });
            UPDATE_OBJECT_LIST.stream().forEach((entity) -> {
                entity.update(con);            });
            DELETE_OBJECT_LIST.stream().forEach((entity) -> {
                entity.delete(con);
            });
            INSERT_OBJECT_LIST.clear();
            UPDATE_OBJECT_LIST.clear();
            DELETE_OBJECT_LIST.clear();
            con.commit();
        } catch (SQLException ex) {
            rollback();
            Logger.getLogger(UnitOfWorkDatabaseMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
