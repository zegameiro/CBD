# CBD Lab 3

Sample workspace for completing the CBD Lab 3.

This workspace provides a docker-compose file to an instance of Cassandra, and it's companions, in a dockerized enviromnment.

The [resources folder](resources) is automatically mounted to `/resources` in the container.
It contains some assets required to complete the Lab.

Open `cqlsh` on the conta
`sudo docker compose exec -it cassandra cqlsh`iner:

Run a cassandra file inside the container:
`sudo docker compose exec -it cassandra cqlsh -f /resources/...`

## Additional Notes

* Make sure you have previously installed [Docker Desktop](https://docs.docker.com/desktop/), or at least Docker Engine.
* [Official Docker Compose tutorial](https://docs.docker.com/compose/gettingstarted/)
