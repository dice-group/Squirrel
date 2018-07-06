import pika

connection = pika.BlockingConnection(pika.ConnectionParameters(
    host='localhost'))
channel = connection.channel()


channel.basic_publish(exchange='', routing_key='ckan', body='hi ckan')

#channel.basic_publish(exchange='', routing_key='ckan', body='https://demo.ckan.org')

print(" [x] Sent 'ckancrawler started'")
connection.close()


#NO NEED FOR THIS EXCEPT FOR TESTING DURING DEPLOYMENT
