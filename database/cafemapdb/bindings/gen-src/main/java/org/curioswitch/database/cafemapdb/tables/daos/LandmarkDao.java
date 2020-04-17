/*
 * MIT License
 *
 * Copyright (c) 2020 Choko (choko@curioswitch.org)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
/*
 * This file is generated by jOOQ.
 */
package org.curioswitch.database.cafemapdb.tables.daos;


import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.processing.Generated;

import org.curioswitch.database.cafemapdb.tables.Landmark;
import org.curioswitch.database.cafemapdb.tables.records.LandmarkRecord;
import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;
import org.jooq.types.ULong;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LandmarkDao extends DAOImpl<LandmarkRecord, org.curioswitch.database.cafemapdb.tables.pojos.Landmark, ULong> {

    /**
     * Create a new LandmarkDao without any configuration
     */
    public LandmarkDao() {
        super(Landmark.LANDMARK, org.curioswitch.database.cafemapdb.tables.pojos.Landmark.class);
    }

    /**
     * Create a new LandmarkDao with an attached configuration
     */
    public LandmarkDao(Configuration configuration) {
        super(Landmark.LANDMARK, org.curioswitch.database.cafemapdb.tables.pojos.Landmark.class, configuration);
    }

    @Override
    public ULong getId(org.curioswitch.database.cafemapdb.tables.pojos.Landmark object) {
        return object.getId();
    }

    /**
     * Fetch records that have <code>id BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<org.curioswitch.database.cafemapdb.tables.pojos.Landmark> fetchRangeOfId(ULong lowerInclusive, ULong upperInclusive) {
        return fetchRange(Landmark.LANDMARK.ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>id IN (values)</code>
     */
    public List<org.curioswitch.database.cafemapdb.tables.pojos.Landmark> fetchById(ULong... values) {
        return fetch(Landmark.LANDMARK.ID, values);
    }

    /**
     * Fetch a unique record that has <code>id = value</code>
     */
    public org.curioswitch.database.cafemapdb.tables.pojos.Landmark fetchOneById(ULong value) {
        return fetchOne(Landmark.LANDMARK.ID, value);
    }

    /**
     * Fetch records that have <code>google_place_id BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<org.curioswitch.database.cafemapdb.tables.pojos.Landmark> fetchRangeOfGooglePlaceId(String lowerInclusive, String upperInclusive) {
        return fetchRange(Landmark.LANDMARK.GOOGLE_PLACE_ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>google_place_id IN (values)</code>
     */
    public List<org.curioswitch.database.cafemapdb.tables.pojos.Landmark> fetchByGooglePlaceId(String... values) {
        return fetch(Landmark.LANDMARK.GOOGLE_PLACE_ID, values);
    }

    /**
     * Fetch a unique record that has <code>google_place_id = value</code>
     */
    public org.curioswitch.database.cafemapdb.tables.pojos.Landmark fetchOneByGooglePlaceId(String value) {
        return fetchOne(Landmark.LANDMARK.GOOGLE_PLACE_ID, value);
    }

    /**
     * Fetch records that have <code>s2_cell BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<org.curioswitch.database.cafemapdb.tables.pojos.Landmark> fetchRangeOfS2Cell(ULong lowerInclusive, ULong upperInclusive) {
        return fetchRange(Landmark.LANDMARK.S2_CELL, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>s2_cell IN (values)</code>
     */
    public List<org.curioswitch.database.cafemapdb.tables.pojos.Landmark> fetchByS2Cell(ULong... values) {
        return fetch(Landmark.LANDMARK.S2_CELL, values);
    }

    /**
     * Fetch records that have <code>type BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<org.curioswitch.database.cafemapdb.tables.pojos.Landmark> fetchRangeOfType(String lowerInclusive, String upperInclusive) {
        return fetchRange(Landmark.LANDMARK.TYPE, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>type IN (values)</code>
     */
    public List<org.curioswitch.database.cafemapdb.tables.pojos.Landmark> fetchByType(String... values) {
        return fetch(Landmark.LANDMARK.TYPE, values);
    }

    /**
     * Fetch records that have <code>created_at BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<org.curioswitch.database.cafemapdb.tables.pojos.Landmark> fetchRangeOfCreatedAt(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRange(Landmark.LANDMARK.CREATED_AT, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>created_at IN (values)</code>
     */
    public List<org.curioswitch.database.cafemapdb.tables.pojos.Landmark> fetchByCreatedAt(LocalDateTime... values) {
        return fetch(Landmark.LANDMARK.CREATED_AT, values);
    }

    /**
     * Fetch records that have <code>updated_at BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<org.curioswitch.database.cafemapdb.tables.pojos.Landmark> fetchRangeOfUpdatedAt(LocalDateTime lowerInclusive, LocalDateTime upperInclusive) {
        return fetchRange(Landmark.LANDMARK.UPDATED_AT, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>updated_at IN (values)</code>
     */
    public List<org.curioswitch.database.cafemapdb.tables.pojos.Landmark> fetchByUpdatedAt(LocalDateTime... values) {
        return fetch(Landmark.LANDMARK.UPDATED_AT, values);
    }
}
