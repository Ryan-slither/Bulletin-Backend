# Bulletin Backend Plan

## API

1. CRUD for Things
2. Creation and Deletion of Likes
3. CRUD for Bulletins attach to Users account
4. Socket for Things & Likes
5. CRUD & Authentication for Users

## DB

1. Things Table - Reference User + Bulletin, Contain time created + content + id
2. Bulletins Table - Reference User, Contain title + time created + member limit + is open + id
3. Likes Table - Reference User + Thing, Contain id
4. Users Table - Contain id + email + hashed password
5. Bulletin Membership Table - Reference User + Bulletin, Contain id

## AFTER DB AND API

1. Create Bulletin join code
2. Create WebSocket for things
3. Create Email verification
4. Endpoint Authorization
