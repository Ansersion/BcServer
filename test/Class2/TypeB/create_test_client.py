import pymysql
import xml.sax
import time
import sys
import re
import subprocess
import hashlib

class TestClientConfigHandler( xml.sax.ContentHandler ):
   def __init__(self):
      self.CurrentData = ""

      # Server 
      self.host = ""
      # User Client Element
      self.user_bin = ""
      self.user_pwd = ""
      self.user_id = ""
      self.post_interval = ""
      self.dev_id = ""
      self.user_output_file = ""
 
      # Device Client Element
      self.device_bin = ""
      self.device_sn = ""
      self.device_pwd = ""
      self.report_interval = ""
      self.device_output_file = ""

   # 元素开始调用
   def startElement(self, tag, attributes):
      self.CurrentData = tag
      if tag == "user_client":
         print ("*****User Client*****")
      elif tag == "device_client":
         print ("*****Device Client*****")
      elif tag == "bc_server":
         print ("*****BC Server*****")

   # 元素结束调用
   def endElement(self, tag):
      if self.CurrentData == "user_bin":
         print ("user_bin:", self.user_bin)
      elif self.CurrentData == "user_pwd":
         print ("user_pwd:", self.user_pwd)
      elif self.CurrentData == "user_id":
         print ("user_id:", self.user_id)
      elif self.CurrentData == "post_interval":
         print ("post_interval:", self.post_interval)
      elif self.CurrentData == "dev_id":
         print ("dev_id:", self.dev_id)
      elif self.CurrentData == "user_output_file":
         print ("user_output_file:", self.user_output_file)
      elif self.CurrentData == "device_bin":
         print ("device_bin:", self.device_bin)
      elif self.CurrentData == "device_sn":
         print ("device_sn:", self.device_sn)
      elif self.CurrentData == "device_pwd":
         print ("device_pwd:", self.device_pwd)
      elif self.CurrentData == "report_interval":
         print ("report_interval:", self.report_interval)
      elif self.CurrentData == "device_output_file":
         print ("device_output_file:", self.device_output_file)
      elif self.CurrentData == "host":
         print ("host:", self.host)
      self.CurrentData = ""

   # 读取字符时调用
   def characters(self, content):
      if self.CurrentData == "user_bin":
         self.user_bin = content
      elif self.CurrentData == "user_pwd":
         self.user_pwd = content
      elif self.CurrentData == "user_id":
         self.user_id = content
      elif self.CurrentData == "user_pwd":
         self.user_pwd = content
      elif self.CurrentData == "post_interval":
         self.post_interval = content
      elif self.CurrentData == "dev_id":
         self.dev_id = content
      elif self.CurrentData == "user_output_file":
         self.user_output_file = content
      elif self.CurrentData == "device_bin":
         self.device_bin = content
      elif self.CurrentData == "device_sn":
         self.device_sn = content
      elif self.CurrentData == "device_pwd":
         self.device_pwd = content
      elif self.CurrentData == "report_interval":
         self.report_interval = content
      elif self.CurrentData == "device_output_file":
         self.device_output_file = content
      elif self.CurrentData == "host":
         self.host = content

# 1. read config.xml
# 2. read 'user' and 'password' records
# 3. create clients

if ( __name__ == "__main__" ):
    # 1. read config.xml
    # 创建一个 XMLReader
    parser = xml.sax.make_parser()
    # 关闭命名空间
    parser.setFeature(xml.sax.handler.feature_namespaces, 0)

    # 重写 ContextHandler
    Handler = TestClientConfigHandler()
    parser.setContentHandler( Handler )

    parser.parse("config.xml")

    # 2. create the clients
    try:
        with open('clients_pid.dat', mode='w') as file:
            # create the user client
            pro = subprocess.Popen([Handler.user_bin, Handler.host, Handler.user_id, hashlib.sha256(Handler.user_pwd.encode("utf-8")).hexdigest(), Handler.post_interval, Handler.dev_id, Handler.user_output_file, "1"])
            file.write("User: " + str(pro.pid) + "\r\n")
            time.sleep(3)

            # create the device client
            pro = subprocess.Popen([Handler.device_bin, Handler.host, Handler.device_sn, Handler.device_pwd, Handler.report_interval, Handler.device_output_file, "1"])
            file.write("Device: " + str(pro.pid) + "\r\n")

    except Exception as e:
        print (e)
        sys.exit()


    while(True):
        sleepSeconds = 10
        time.sleep(sleepSeconds)
        print("sleep " + str(sleepSeconds))

