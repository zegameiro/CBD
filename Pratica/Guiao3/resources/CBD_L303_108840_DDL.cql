CREATE KEYSPACE music_hub WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1} AND durable_writes = true;

USE music_hub;

CREATE TABLE artists (
    artist_id uuid,
    name text,
    genres set<text>,
    details map<text, text>,
    timestamp_create timestamp,

    PRIMARY KEY (artist_id, timestamp_create)
) WITH CLUSTERING ORDER BY (timestamp_create ASC);


CREATE TABLE albums (
    album_id uuid,
    title text,
    release_year int,
    artist text,
    timestamp_added_album timestamp,

    PRIMARY KEY ((title, artist), timestamp_added_album)
) WITH CLUSTERING ORDER BY (timestamp_added_album ASC);

CREATE TABLE songs_artist (
    song_id uuid,
    title text,
    duration int,
    artist text,
    album text,
    timestamp_added_song timestamp,

    PRIMARY KEY (artist, timestamp_added_song)
) WITH CLUSTERING ORDER BY (timestamp_added_song ASC);

CREATE TABLE songs_album (
    song_id uuid,
    title text,
    duration int,
    artist text,
    album text,
    timestamp_added_song timestamp,

    PRIMARY KEY (album, timestamp_added_song)
) WITH CLUSTERING ORDER BY (timestamp_added_song ASC);

CREATE TABLE review_albums (
    review_id uuid,
    album text,
    rating list<int>,
    comments list<text>,

    PRIMARY KEY (album)
);

CREATE INDEX ON artists(genres);
CREATE INDEX ON albums(title);