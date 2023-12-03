CREATE KEYSPACE videotube 
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 2}  AND durable_writes = true;

USE videotube;

CREATE TABLE user (
    user_id uuid,
    username text,
    name text,
    email text,
    timestamp_regist timestamp,
    
    PRIMARY KEY (user_id)
);

CREATE TABLE video (
    video_id uuid,
    author text,
    name text,
    description text,
    duration int,
    tags set<text>,
    timestamp_upload_part timestamp,

    PRIMARY KEY ((author), timestamp_upload_part)
) WITH CLUSTERING ORDER BY (timestamp_upload_part DESC);

CREATE TABLE comment_author (
    comment_id uuid,
    video text,
    auth_comm text,
    comment text,
    timestamp_comment timestamp,

    PRIMARY KEY (auth_comm, timestamp_comment)
) WITH CLUSTERING ORDER BY (timestamp_comment DESC);

CREATE TABLE comment_video (
    comment_id uuid,
    video text,
    auth_comm text,
    comment text,
    timestamp_comment timestamp,

    PRIMARY KEY (video, timestamp_comment)
) WITH CLUSTERING ORDER BY (timestamp_comment DESC);

CREATE TABLE follower (
    video text,
    followers set<text>,
    
    PRIMARY KEY (video)
);

CREATE TABLE event (
    user text,
    video text,
    type text,
    moment int,
    timestamp_event timestamp,

    PRIMARY KEY ((user, video), timestamp_event)
);

CREATE TABLE rating_video (
    rating_id uuid,
    video text,
    rating tinyint,

    PRIMARY KEY (video, rating_id)
);

CREATE INDEX ON video(tags);



