mkdir %CD%\docker-volumes\openfire

docker run --name openfire -d --publish 9090:9090 --publish 5222:5222 --publish 7777:7777 --volume %CD%/docker-volumes/openfire:/var/lib/openfire sameersbn/openfire:3.10.3-19