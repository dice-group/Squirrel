default:
	sudo rm -rf data/
	docker-compose up -d
	docker-compose logs -f

network:
	docker network create -d bridge -o com.docker.network.bridge.enable_ip_masquerade=true --attachable --subnet 172.17.200.0/24 squirrel
