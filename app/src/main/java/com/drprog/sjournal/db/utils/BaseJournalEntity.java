package com.drprog.sjournal.db.utils;

/**
 * Created by Romka on 28.07.2014.
 */
public abstract class BaseJournalEntity {
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
