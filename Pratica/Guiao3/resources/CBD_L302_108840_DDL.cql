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
    author_id uuid,
    name text,
    description text,
    duration int,
    tags set<text>,
    timestamp_upload_part timestamp,

    PRIMARY KEY ((author_id), timestamp_upload_part)
) WITH CLUSTERING ORDER BY (timestamp_upload_part DESC);

CREATE TABLE comment_author (
    comment_id uuid,
    video_id uuid,
    auth_comm_id uuid,
    comment text,
    timestamp_comment timestamp,

    PRIMARY KEY (auth_comm_id, timestamp_comment)
) WITH CLUSTERING ORDER BY (timestamp_comment DESC);

CREATE TABLE comment_video (
    comment_id uuid,
    video_id uuid,
    auth_comm_id uuid,
    comment text,
    timestamp_comment timestamp,

    PRIMARY KEY (video_id, timestamp_comment)
) WITH CLUSTERING ORDER BY (timestamp_comment DESC);

CREATE TABLE follower (
    video_id uuid,
    follower_id set<uuid>,
    
    PRIMARY KEY (video_id)
);

CREATE TABLE event (
    user_id uuid,
    video_id uuid,
    type text,
    moment int,
    timestamp_event timestamp,

    PRIMARY KEY ((user_id, video_id), timestamp_event)
);

CREATE TABLE rating_video (
    rating_id uuid,
    video_id uuid,
    rating tinyint,

    PRIMARY KEY (video_id, rating_id)
);

CREATE INDEX ON video(tags);



