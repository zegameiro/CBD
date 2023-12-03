-- Insert operations

INSERT INTO music_hub.artists (artist_id, name, genres, details, timestamp_create) VALUES (00000000-0000-0000-0000-000000000140, 'Muse', {'Pop', 'Rock', 'Indie'}, {'country': ' Teignmouth, Devon UK', 'description': 'Muse have won numerous awards, including two Grammy Awards, two Brit Awards, five MTV Europe Music Awards and eight NME Awards.', 'date_formation': '1994'}, toTimestamp(now()));
INSERT INTO music_hub.albums (album_id, title, release_year, artist, timestamp_added_album) VALUES (00000000-0000-0000-0210-000000000000, 'Drones', 2015, 'Muse', toTimestamp(now()));
INSERT INTO music_hub.songs_artist (song_id, title, duration, artist, album, timestamp_added_song) VALUES (00000000-0000-0260-0000-000000000000, 'The Globalist', 6473231, 'Muse', 'Drones', toTimestamp(now()));
INSERT INTO music_hub.review_albums (review_id, album, rating, comments) VALUES (00000000-0130-0000-0000-000000000000, 'Drones', [8,9], ['I like how they change their style to something more heavy in this album', 'I really like the riff in the second song of the album']);


-- Update operations

UPDATE music_hub.artists SET genres = {'Indie', 'Pop', 'Rock', 'Alternative'} WHERE artist_id = 00000000-0000-0000-0000-000000000140 AND timestamp_create = '2023-11-29 21:19:02.517000+0000';
UPDATE music_hub.albums SET release_year = 2016 WHERE title = 'Drones' AND artist = 'Muse' AND timestamp_added_album = '2023-11-29 21:19:02.520000+0000';
UPDATE music_hub.review_albums SET rating = [8,9,10], comments = ['I like how they change their style to something more heavy in this album', 'I really like the riff in the second song of the album', 'This album really rad'] WHERE album = 'Drones';
UPDATE music_hub.songs_artist SET duration = 74536823 WHERE artist = 'Muse' AND timestamp_added_song = '2023-11-29 21:19:02.521000+0000';
UPDATE music_hub.songs_album SET duration = 74536823 WHERE album = 'Drones' AND timestamp_added_song = '2023-11-29 21:19:02.521000+0000';


-- Delete operations

DELETE FROM music_hub.review_albums WHERE album = 'Drones';
DELETE FROM music_hub.review_albums WHERE album = 'With Teeth';
DELETE FROM music_hub.songs_artist WHERE artist = 'Pearl Jam' AND timestamp_added_song = '2023-11-29 21:19:02.521000+0000';
DELETE FROM music_hub.songs_album WHERE album = 'Vs.' AND timestamp_added_song = '2023-11-29 21:19:02.521000+0000';
DELETE FROM music_hub.albums WHERE title = 'Vs.' AND artist = 'Pearl Jam' AND timestamp_added_album = '2023-11-29 21:19:02.521000+0000';


-- Queries

-- 1. Get all the albums of an artist
SELECT * FROM music_hub.albums WHERE artist = 'Green Day' ALLOW FILTERING;

-- 2. Get all the songs from an album
SELECT * FROM music_hub.songs_album WHERE album = 'American Idiot';

-- 3. Get all the songs from an artist
SELECT * FROM music_hub.songs_artist WHERE artist = 'Green Day';

-- 4. Get all the reviews from an album
SELECT * FROM music_hub.review_albums WHERE album = 'All Killer No Filler';

-- 5. Count the number of songs from an album
SELECT COUNT(*) AS num_songs FROM music_hub.songs_album WHERE album = 'American Idiot';

-- 6. Count the total number of songs saved in the database
SELECT COUNT(*) AS total_songs FROM music_hub.songs_album;

-- 7. Get the albums with release_year bigger than 2000;
SELECT * FROM music_hub.albums WHERE release_year > 2000 ALLOW FILTERING;

-- 8. Get the last 2 songs added by an artist
SELECT * FROM music_hub.songs_artist WHERE artist = 'Green Day' LIMIT 2;

-- 9. Get the last album added by an artist
SELECT * FROM music_hub.albums WHERE artist = 'Foo Fighters' LIMIT 1 ALLOW FILTERING;

-- 10. Get the users that were added after a certain date (for example, 2015-08-12 13:10:02.523100+0000)
SELECT * FROM music_hub.artists WHERE timestamp_create > '2015-08-12 13:10:02.523100+0000' ALLOW FILTERING;


