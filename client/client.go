package main

import (
	"flag"
	"fmt"
	"net"
)

func main() {

	// resolve command line arguments
	var request_type string
	var interval_time int
	flag.StringVar(&request_type, "type", "idempotence", "request_type")
	flag.IntVar(&interval_time, "t", 30, "interval_time")
	flag.Parse()
	// fmt.Println("request_type", request_type)
	// fmt.Println("interval_time", interval_time)

	// build udp connection
	socket, err := net.DialUDP("udp", nil, &net.UDPAddr{
		IP:   net.IPv4(172, 20, 10, 6),
		Port: 12345,
	})
	if err != nil {
		fmt.Println("connect server fail, err:", err)
		return
	}
	defer socket.Close()

	sendData := []byte("/Users/jiaweiyao/Documents/GitHub/distributed-system/test.txt,30")
	_, err = socket.Write(sendData) // 发送数据
	if err != nil {
		fmt.Println("send data fail, err:", err)
		return
	}
	data := make([]byte, 4096)
	n, remoteAddr, err := socket.ReadFromUDP(data) // 接收数据
	if err != nil {
		fmt.Println("recv data fail, err:", err)
		return
	}
	fmt.Printf("recv:%v addr:%v count:%v\n", string(data[:n]), remoteAddr, n)
}
