import psycopg2
import os
import time

print("waiting for 30 seconds")
time.sleep(30)
print("wait complete. starting the script")

try:
    connection = psycopg2.connect(user=os.getenv('database.user', 'postgres'),
                                    password=os.getenv('database.password', 'welcome'),
                                    host=os.getenv('database.host', '172.20.50.167'),
                                    port=os.getenv('database.port', '5432'),
                                    database=os.getenv('database.name', 'postgres'))
    cursor = connection.cursor()

    absFileDir = os.path.dirname(os.path.abspath(__file__))
    print("absolute path of the script:", absFileDir)

    for file in os.listdir(absFileDir + '/sqls'):
        print("reading the file :", file)
        if file.endswith('.sql'):
            fo = open(absFileDir + '/sqls/' + file, 'r')
            script = fo.read()
            print("path of the file:", absFileDir + '/sqls/' + file)
            print("executing script :", script)
            cursor.execute(script)

    connection.commit()
except (Exception, psycopg2.Error) as error:
    if(connection):
        print("Failed to insert record into monitoring table", error)
finally:
    if(connection):
        cursor.close()
        connection.close()
        print("Postgres connection closed")
