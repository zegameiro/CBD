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

            // System.out.println("Insert new data in the Cassandra Data Base videotube");

            // System.out.println("    Insert a new user");
            // session.execute("INSERT INTO videotube.user (user_id, username, name, email, timestamp_regist) VALUES (00000000-0000-0000-0000-000000000021, 'ricfazeres', 'Ricardo Costa', 'ricardoc@ua.pt', toTimeStamp(now()));");
            // System.out.println("    User inserted with success\n");

            // System.out.println("    ------------------------------------------------------------");

            // System.out.println("    Insert a new video");
            // session.execute("INSERT INTO videotube.video (video_id, author_id, name, description, duration, tags, timestamp_upload_part) VALUES (00000000-0000-0000-0023-000000000000, 00000000-0000-0000-0000-000000000021, 'How to build your own shelf', 'Build youre own shelf in 5 minutes it will blow your mind', 398402, {'furniture', 'tutorial'}, '2023-10-09 14:32:56.003400+0000');");
            // System.out.println("    Video inserted with success\n");

            // System.out.println("    ------------------------------------------------------------");
            // System.out.println("    Insert a new rating");
            // session.execute("INSERT INTO videotube.rating_video (rating_id, video_id, rating) VALUES (00000000-0012-0000-0000-000000000000, 00000000-0000-0000-0023-000000000000, 5);");
            // System.out.println("    Rating inserted with success\n");

            // System.out.println("--------------------------------------------------------------------------------------------\n");

            // System.out.println("Edition of a data stored in the Cassandra Data Base videotube");

            // System.out.println("    Edit the name of a user:");
            // session.execute("UPDATE videotube.user SET name = 'Ricard Costa Cinta', email = 'ricardoccinta@ua.pt' WHERE user_id = 00000000-0000-0000-0000-000000000021;");
            // System.out.println("    User edited with success\n");

            // System.out.println("    ------------------------------------------------------------");

            // System.out.println("    Edit the name of a video:");
            // session.execute("UPDATE videotube.video SET name = 'How to build your own shelf in 5 minutes', description = 'Build youre own shelf in 5 minutes it will blow your mind so badly' WHERE author_id = 00000000-0000-0000-0000-000000000021 AND timestamp_upload_part = '2023-10-09 14:32:56.003400+0000';");
            // System.out.println("    Video edited with success\n");

            // System.out.println("    ------------------------------------------------------------");

            // System.out.println("    Edit the rating of a video:");
            // session.execute("UPDATE videotube.rating_video SET rating = 4 WHERE video_id = 00000000-0000-0000-0023-000000000000 AND rating_id = 00000000-0012-0000-0000-000000000000;");
            // System.out.println("    Rating edited with success\n");

            // System.out.println("--------------------------------------------------------------------------------------------\n");

            // System.out.println("Search for data stored in the Cassandra Data Base videotube");

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
            
            ResultSet searchResult2 = session.execute("SELECT * FROM videotube.video WHERE author_id = 00000000-0000-0000-0000-000000000020;");

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

             System.out.println("    Search for the commentaries of a user by descending order of the timestamp_comment:\n");

        }
    }
}