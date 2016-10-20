Doodle-like REST API backed by Cassandra, used for Fribourg's University Advanced Databases System Course
----

# Context

The goal of the exercise is to build a REST API that will be able to create a poll, 
subscribe to the poll and retrieve the subscriptions.

In REST notation:

## Create a new poll

```
    POST /rest/polls
    { "label": "Afterwork", "choices": [ "Monday", "Tuesday", "Friday" ], "email": "benoit@noisette.ch" }
```

Returns `pollId` (in `Location` header)

## Subscribe to the poll

```
	PUT /rest/polls/<pollId>
	{ "label": "Benoit", "choices": [ "Monday", "Friday" ] }
```

Returns the updated poll, JSON encoded (see below)

```
	GET /rest/polls/<pollId>
```

Returns the poll, JSON encoded

```
	{ "poll": { "label": "Afterwork", "choices": [ "Monday", "Tuesday", "Friday" ], "email": "benoit@noisette.ch", "subscribers": [ { "name": "Benoit", "choices": [ "Monday", "Friday" ] }, ... ] } }
```

# Proposed Data Model

## Keyspace

```
CREATE KEYSPACE doodle 
    WITH replication = {'class': 'org.apache.cassandra.locator.SimpleStrategy',
        'replication_factor': 3 };
```

## Table and Type Definition

```
CREATE TYPE doodle.subscriber (
  label text,
  choices list<text>
);
```

```
CREATE TABLE doodle.polls ( 
    id UUID PRIMARY KEY,
    label text,
    choices list<text>,
    email text,
    maxChoices int,
    subscribers list<FROZEN <subscriber>>
);
```

## Inserting a poll

```
// Create a new poll
INSERT INTO polls (id, label, email, choices) 
    VALUES (now(), 'Test poll1', 'benoit@noisette.ch', ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday']);
```

```
// Retrieve the UUID generated
SELECT id FROM polls;
```

```
// Add a subscriber (please replace the UUID )
UPDATE polls SET subscribers = subscribers + [ { label: 'Benoit', choices: ['Monday', 'Friday'] } ]
    WHERE id = d8801c20-96aa-11e6-8d5f-938e80c31810;
```

```
// Add many subscribers at the same time
UPDATE polls SET subscribers = 
    subscribers + [ { label: 'Maxime', choices: ['Monday', 'Wednesday', 'Friday'] } ,
        { label: 'Nicolas', choices: ['Wednesday', 'Thursday', 'Friday'] } ] 
    WHERE id = d8801c20-96aa-11e6-8d5f-938e80c31810;
```

```
// Remove one subscriber
DELETE subscribers[0] FROM Polls WHERE id = d8801c20-96aa-11e6-8d5f-938e80c31810;
```
