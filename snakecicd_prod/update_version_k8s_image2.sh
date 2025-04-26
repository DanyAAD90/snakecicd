#!/bin/bash

liczba=$(cat /home/tms_master_1/snakecicd_monitor/latest_version)
sed -i "s/SNAKE_VERSION/$liczba/" /home/tms_master_1/snake/snakecicd_prod/values.yaml

#kubectl delete svc snakecicd-service --namespace snakecicd-prod
sed -i -E "s/(versionapp: )[0-9]+/\1$liczba/" /home/tms_master_1/snake/snakecicd_prod/values.yaml

helm upgrade snakecicd-prod /home/tms_master_1/snake/snakecicd_prod/ -f /home/tms_master_1/snake/snakecicd_prod/values.yaml --namespace snakecicd-prod
kubectl patch svc snakecicd-service -n snakecicd-prod -p '{"spec":{"externalIPs":["192.168.18.165"]}}'
echo "Obecna wersja image na deploy kubernetes po zmianie:"
echo "----------------------------------------------------"
kubectl -n snakecicd-prod get deploy snakecicd-prod -o jsonpath='{.spec.template.spec.containers[0].image}'
#kubectl -n snakecicd-prod get all
echo ""
echo "-----------------------------------------------------------------------"
echo "Aplikacja Snake zostala pomyslnie zaktualizowana do wersji 1.${liczba}!"
echo "Możesz teraz zagrać w mnią tutaj http://192.168.18.165:8321 :)"
echo "Środowisko produkcyjne to kubernetes z dwoma replikami w kubernetes)"
echo "-----------------------------------------------------------------------"
