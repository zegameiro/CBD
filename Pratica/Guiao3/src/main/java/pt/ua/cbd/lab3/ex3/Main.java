package pt.ua.cbd.lab3.ex3;

import java.util.HashSet;
import java.util.Set;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

public class Main {
    public static void main(String[] args) {
        try (CqlSession session = CqlSession.builder().build()) {                                  // (1)
            
            ResultSet rs = session.execute("select release_version from system.local");              // (2)
            Row row = rs.one();
            System.out.println(row.getString("release_version") + '\n');                                    // (3)

            System.out.println("---------------------------------------------------------- a) Insert, Update and search operations ----------------------------------------------------------");

            System.out.println("Insert new data in the Cassandra Data Base videotube");

            System.out.println("    Insert a new user");
            session.execute("INSERT INTO videotube.user (user_id, username, name, email, timestamp_regist) VALUES (00000000-0000-0000-0000-000000000021, 'ricfazeres', 'Ricardo Costa', 'ricardoc@ua.pt', toTimeStamp(now()));");
            System.out.println("    User inserted with success\n");

            System.out.println("    ------------------------------------------------------------");

            System.out.println("    Insert a new video");
            session.execute("INSERT INTO videotube.video (video_id, author, name, description, duration, tags, timestamp_upload_part) VALUES (00000000-0000-0000-0023-000000000000, 'ricfazeres', 'How to build your own shelf', 'Build youre own shelf in 5 minutes it will blow your mind', 398402, {'furniture', 'tutorial'}, '2023-10-09 14:32:56.003400+0000');");
            System.out.println("    Video inserted with success\n");

            System.out.println("    ------------------------------------------------------------");
            System.out.println("    Insert a new rating");
            session.execute("INSERT INTO videotube.rating_video (rating_id, video, rating) VALUES (00000000-0012-0000-0000-000000000000, 'How to build your own shelf', 5);");
            System.out.println("    Rating inserted with success\n");

            System.out.println("--------------------------------------------------------------------------------------------\n");

            System.out.println("Edition of a data stored in the Cassandra Data Base videotube");

            System.out.println("    Edit the name of a user:");
            session.execute("UPDATE videotube.user SET name = 'Ricard Costa Cinta', email = 'ricardoccinta@ua.pt' WHERE user_id = 00000000-0000-0000-0000-000000000021;");
            System.out.println("    User edited with success\n");

            System.out.println("    ------------------------------------------------------------");

            System.out.println("    Edit the name of a video:");
            session.execute("UPDATE videotube.video SET name = 'How to build your own shelf in 5 minutes', description = 'Build youre own shelf in 5 minutes it will blow your mind so badly' WHERE author = 'ricfazeres' AND timestamp_upload_part = '2023-10-09 14:32:56.003400+0000';");
            System.out.println("    Video edited with success\n");

            System.out.println("    ------------------------------------------------------------");

            System.out.println("    Edit the rating of a video:");
            session.execute("UPDATE videotube.rating_video SET rating = 4 WHERE video = 'How to build your own shelf in 5 minutes' AND rating_id = 00000000-0012-0000-0000-000000000000;");
            System.out.println("    Rating edited with success\n");

            System.out.println("--------------------------------------------------------------------------------------------\n");

            System.out.println("Search for data stored in the Cassandra Data Base videotube");

            System.out.println("    Search for users:\n");

            String username = null;
            String name = null;
            String email = null;

            ResultSet searchResult = session.execute("SELECT * FROM videotube.user;");
            System.out.printf("%-15s | %-25s | %-25s%n", "Username", "Name", "Email");
            System.out.println("----------------+---------------------------+-------------------------");

            for (Row rowSearch : searchResult) {
                username = rowSearch.getString("username");
                name = rowSearch.getString("name");
                email = rowSearch.getString("email");

                System.out.printf("%-15s | %-25s | %-25s%n", username, name, email);
            }

            System.out.println();

            System.out.println("------------------------------------------------------------\n");

            System.out.println("    Search for all the videos of an author:\n");
            String videoName = null;
            String description = null;
            int duration = 0;
            Set<String> tags = new HashSet<String>();
            
            ResultSet searchResult2 = session.execute("SELECT * FROM videotube.video WHERE author = 'ma';");

            for (Row rowSearch : searchResult2) {
                videoName = rowSearch.getString("name");
                description = rowSearch.getString("description");
                duration = rowSearch.getInt("duration");
                tags = rowSearch.getSet("tags", String.class);

                System.out.println("    Video name: " + videoName);
                System.out.println("    Description: " + description);
                System.out.println("    Duration: " + duration);
                System.out.println("    Tags: " + tags);
                System.out.println();
            }

            System.out.println("------------------------------------------------------------\n");

            System.out.println("---------------------------------------------------------- b) QUERIES ----------------------------------------------------------\n");

            System.out.println("Query 1 - Get the last 3 comments inputed in a video:\n");
            ResultSet queryResult1 = session.execute("SELECT * FROM videotube.comment_video WHERE video = 'How to take care of your drumkit' LIMIT 3;");

            for(Row comment : queryResult1) {
                String video = comment.getString("video");
                String auth_comm = comment.getString("auth_comm");
                String comm = comment.getString("comment");

                System.out.println("    Video: " + video);
                System.out.println("    Author: " + auth_comm);
                System.out.println("    Comment: " + comm);
                System.out.println();
            }

            System.out.println("------------------------------------------------------------\n");

            System.out.println("Query 2 - List the tags of a video:\n");

            ResultSet queryResult2 = session.execute("SELECT name, tags FROM videotube.video WHERE author = 'jl' and timestamp_upload_part = '2023-11-29 01:07:32.121000+0000';");

            for(Row tag : queryResult2) {
                Set<String> tags2 = tag.getSet("tags", String.class);
                System.out.println("    Video: " + tag.getString("name"));
                System.out.println("    Tags: " + tags2);
                System.out.println();
            }

            System.out.println("------------------------------------------------------------\n");

            System.out.println("Query 4 - Get the last 5 events of a video made by a user\n");

            ResultSet queryResult3 = session.execute("SELECT * FROM videotube.event WHERE user = 'ma' AND video = 'American Idiot' LIMIT 5;");

            for(Row tag : queryResult3) {
                System.out.println("    User: " + tag.getString("user"));
                System.out.println("    Video: " + tag.getString("video"));
                System.out.println("    Type: " + tag.getString("type"));
                System.out.println("    Moment: " + tag.getInt("moment"));
                System.out.println();
            }

            System.out.println("------------------------------------------------------------\n");
            
            System.out.println("Query 5 - Get the shared videos by a user (for example maria1987) in a period of time (for example August 2017)\n");

            ResultSet queryResult4 = session.execute("SELECT * FROM videotube.video WHERE author = 'ma' AND timestamp_upload_part >= '2017-08-01 00:00:00.000000+0000';");

            for(Row tag : queryResult4) {
                System.out.println("    Author: " + tag.getString("author"));
                System.out.println("    Name: " + tag.getString("name"));
                System.out.println("    Description: " + tag.getString("description"));
                System.out.println("    Duration: " + tag.getInt("duration"));
                System.out.println("    Tags: " + tag.getSet("tags", String.class));
                System.out.println();
            }
        }
    }
}