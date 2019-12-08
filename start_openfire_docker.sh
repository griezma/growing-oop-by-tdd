mkdir $PWD/docker-volumes

docker run --name openfire -d --restart=always \
  --publish 9090:9090 --publish 5222:5222 --publish 7777:7777 \
  --volume $PWD/docker-volumes/openfire:/var/lib/openfire \
  sameersbn/openfire:3.10.3-19