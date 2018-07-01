import pika

connection = pika.BlockingConnection(pika.ConnectionParameters(
    host='localhost'))
channel = connection.channel()


#channel.queue_declare(queue='ckan')

channel.basic_publish(exchange='',
                      routing_key='ckan',
                      body='https://www.facebook.com')

print(" [x] Sent 'ckancrawler started'")
connection.close()


#TODO: NO NEED FOR THIS EXCEPT FOR TESTING DURING DEPLOYMENT
