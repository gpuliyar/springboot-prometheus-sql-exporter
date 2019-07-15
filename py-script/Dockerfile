FROM python:3-stretch

WORKDIR /opt/pyscript

ADD . .

RUN pip install psycopg2

ENTRYPOINT [ "python", "main.py" ]
