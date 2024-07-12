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

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * THIS CLASS IS A PART OF THE OBJECT-TRACKING-MODULE
 *
 * @author Maurice Amon
 * @version 1.1
 */
public class SequenceEntity implements Entity {

    // The relative path for the sequences ... 
    private final String RELATIVE_SEQUENCE_PATH = "media/sequences/";
    // I use a SimpleDateFormat as the required property, because the Date-class of the Java-API is already depreceated ...
    private Date date;

    public SequenceEntity() {

    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public String getRelativePath() {
        return RELATIVE_SEQUENCE_PATH;
    }

    /* This function should insert the object as a record in our database ...
       @param con Connection 
     */
    @Override
    public void insert(Connection con) {
        try {
            Random random = new Random();
            Integer id = Math.abs(random.nextInt(100000000 - 100 + 1) + 100);
            SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.sql.Date sqlDate = new java.sql.Date(sqlFormat.parse(sqlFormat.format(date)).getTime());
            PreparedStatement stmt = con.prepareStatement("INSERT INTO APP.SEQUENCE VALUES (" + id + ", '" + RELATIVE_SEQUENCE_PATH + date.toString() + "', ?)");
            stmt.setDate(1, sqlDate);
            stmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(IpCameraEntity.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(SequenceEntity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void update(Connection con) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Connection con) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
