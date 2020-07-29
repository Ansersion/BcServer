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
      # Client Element
      self.bin = ""
      self.server = ""
      self.ping_interval = ""
      self.create_interval = ""

   # 元素开始调用
   def startElement(self, tag, attributes):
      self.CurrentData = tag
      if tag == "client":
         print ("*****Test Client*****")

   # 元素结束调用
   def endElement(self, tag):
      if self.CurrentData == "bin":
         print ("bin:", self.bin)
      elif self.CurrentData == "server":
         print ("server:", self.server)
      elif self.CurrentData == "ping_interval":
         print ("ping_interval:", self.ping_interval)
      elif self.CurrentData == "create_interval":
         print ("create_interval:", self.create_interval)
      self.CurrentData = ""

   # 读取字符时调用
   def characters(self, content):
      if self.CurrentData == "bin":
         self.bin = content
      elif self.CurrentData == "server":
         self.server = content
      elif self.CurrentData == "ping_interval":
         self.ping_interval = content
      elif self.CurrentData == "create_interval":
         self.create_interval = content

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

    # 2. read 'user' and 'password' records
    userList = []
    user2PwdDict = {}
    try:
        pattern = re.compile(r'(\w+):(\w+)')
        with open('user_password.dat', mode='r') as file:
            for line in file.readlines():
                m = pattern.search(line)
                if m:
                    user = m.group(1)
                    pwd = m.group(2)
                    userList.append(user)
                    user2PwdDict[user] = pwd
                else:
                    print(line + ":" + "NOT MATCHED")
    except Exception as e:
        print (e)
        sys.exit()

    # 3. create clients
    try:
        with open('clients_pid.dat', mode='w') as file:
            pro = None
            for userTmp in userList:
                time.sleep(float(Handler.create_interval)/1000)
                pro = subprocess.Popen([Handler.bin, Handler.server, userTmp, hashlib.sha256(user2PwdDict[userTmp].encode("utf-8")).hexdigest(), Handler.ping_interval])
                file.write(userTmp + ":" + user2PwdDict[userTmp] + "->" + str(pro.pid) + "\r\n")
    except Exception as e:
        print (e)
        sys.exit()


    while(True):
        sleepSeconds = 10
        time.sleep(sleepSeconds)
        print("sleep " + str(sleepSeconds))

