#!/usr/bin/python
# -*- coding: UTF-8 -*-

import pymysql


db = getDB



# class MySql

class AutoCloseCursorConnection(object):
    cursor = None
    conn = None

    def __init__(self, conn):
        self.conn = conn

    def __getattr__(self, key):
        return getattr(self.conn, key)

    def cursor(self, *args, **kwargs):
        self.cursor = self.conn.cursor(*args, **kwargs)
        return self.cursor

    def close(self):
        if self.cursor:
            self.cursor.close()
        self.conn.close()













##############################################################

db = pymysql.connect('localhost','root','123456','shadow')

cursor = db.cursor()

cursor.execute("SELECT VERSION()")
data = cursor.fetchone()
print("database version : %s" %data)

db.close()