// 7. Permitir a pesquisa de todos os videos de determinado autor;
SELECT JSON * FROM videotube.video WHERE author = 'ms';

// 8. Permitir a pesquisa de comentarios por utilizador, ordenado inversamente pela data;
SELECT JSON * FROM videotube.comment_author WHERE auth_comm = 'gm';

// 9. Permitir a pesquisa de comentarios por videos, ordenado inversamente pela data;
SELECT JSON * FROM videotube.comment_video  WHERE video = 'How to take care of your drumkit';

// 10. Permitir a pesquisa do rating medio de um video e quantas vezes foi votado;
SELECT JSON COUNT(video) AS "votes", AVG(rating) AS "average rating" FROM videotube.rating_video WHERE video = 'American Idiot';


// 1. Os ultimos 3 comentarios introduzidos para um video;
SELECT * FROM videotube.comment_video WHERE video = 'How to take care of your drumkit' LIMIT 3;

// 2. Lista das tags de determinado video;
SELECT tags FROM videotube.video WHERE author = 'jl' and timestamp_upload_part = '2023-11-29 01:07:32.121000+0000';

// 3. Todos os videos com a tag Aveiro;
// Esta querie não funciona, pois para pesquisar por tags é necessário pesquisar primeiro pelo id do autor do vídeo e isso iria retornar apenas os vídeos de um autor em específico com a tag Aveiro e não todos os vídeos.
// No entanto se usarmos a opção de ALLOW FILTERING, a querie funciona, mas não é recomendado usar esta opção.
SELECT * FROM videotube.video WHERE tags CONTAINS 'Aveiro' ALLOW FILTERING; 


// 4. Os ultimos 5 eventos de determinado video realizados por um utilizador;
SELECT * FROM videotube.event WHERE user = 'ma' AND video = 'American Idiot' LIMIT 5;

// 5. Videos partilhados por determinado utilizador (maria1987, por exemplo) num determinado periodo de tempo (Agosto de 2017, por exemplo);
SELECT * FROM videotube.video WHERE author = 'ma' AND timestamp_upload_part >= '2017-08-01 00:00:00.000000+0000';

// 6. Os ultimos 10 videos, ordenado inversamente pela data da partilhada;
// Esta querie também não é possível realizar, pois para pesquisarmos os últimos 10 vídeos e ordená-los por ordem decrescente teríamos de especificar o autor do vídeo e isso não iria retornar o que a querie pedia.

// 7. Todos os seguidores (followers) de determinado video;
SELECT followers FROM videotube.follower WHERE video = 'Sum 41 Confirmed for NOS Alive';

// 8. Todos os comentarios (dos videos) que determinado utilizador esta a seguir (following);
// A querie desta alínea também não é possível realizar, visto que o Cassandra não permite fazer JOINS, pois esta permite uma boa performance e escalabilidade, em vez de consistência e normalização.

// 9. Os 5 videos com maior rating;
// Esta querie não é possível realizar sem a opção ALLOW FILTERING, pois a partition key é o id do vídeo e não o valor do rating em si, neste caso se quisessemos que desenvolver uma querie funcionasse sem o ALLOW FILTERING poderiamos apenas pesquisar pelas 5 melhores ratings para um vídeo em específico.
SELECT * FROM videotube.rating_video WHERE rating = 5 ALLOW FILTERING;

// 10. Uma query que retorne todos os videos e que mostre claramente a forma pela qual estao ordenados;
// Esta querie também não é possível realizar, pois para pesquisarmos todos os vídeos e ordená-los por ordem decrescente teríamos de especificar o autor do vídeo e isso não iria retornar o que a querie pedia.

// 11. Lista com as Tags existentes e o numero de videos catalogados com cada uma delas;
// Esta querie também não é possível realizar, visto que a partition key é o id do autor ou seja só poderiamos pesquisar por todas as tags e filtrar os vídeos por tag se especificarmos o autor.

// 12. Número total de comentários feitos num vídeo em específico;
SELECT COUNT(*) AS total_comments FROM videotube.comment_video WHERE video = 'How to take care of your drumkit';

// 13. Número total de eventos que um utilizador em específico fez para um determinado vídeo;
SELECT COUNT(*) AS total_events FROM videotube.event WHERE user = 'ma' AND video = 'American Idiot';