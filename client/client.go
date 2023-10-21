package main

import (
	"flag"
	"fmt"
	"net"
)

// global var
var debug = true
var defaultUUID = "000000000000000000000000000000000000"

func main() {
	// default config
	reqType := "idempotence"
	freshnessInterval := 30 // s
	serverIP := "127.0.0.1"
	serverPort := 6666

	// client local cache
	cache := make(map[string]CacheStruct)

	// parse command line arguments
	flag.BoolVar(&debug, "debug", debug, "debug")
	flag.StringVar(&serverIP, "ip", serverIP, "serverIP")
	flag.IntVar(&serverPort, "p", serverPort, "serverPort")
	flag.StringVar(&reqType, "reqType", reqType, "reqType")
	flag.IntVar(&freshnessInterval, "t", freshnessInterval, "freshnessInterval")
	flag.Parse()

	// build udp connection
	socket, err := net.DialUDP("udp", nil, &net.UDPAddr{
		IP:   net.ParseIP(serverIP),
		Port: serverPort,
	})
	if err != nil {
		fmt.Println("connect server fail, err:", err)
		return
	}
	defer socket.Close()

	// show user interface
interfaceLoop:
	for {
		clearScreen()
		fmt.Println("[ Client info ]")
		fmt.Println("- Server IP:         ", serverIP)
		fmt.Println("- Server port:       ", serverPort)
		fmt.Println("- Request type:      ", reqType)
		fmt.Println("- Freshness interval:", freshnessInterval, "s")
		fmt.Println("")
		fmt.Println("[ Options ]")
		fmt.Println("- 1 Read content")
		fmt.Println("- 2 Insert content")
		fmt.Println("- 3 Register to file")
		fmt.Println("- 0 Exit")
		fmt.Println("")
		fmt.Printf("Your option: ")

		var op string
		fmt.Scanln(&op)
		switch op {
		case "1":
			readContent(socket, cache, freshnessInterval)
		case "2":
			insertContent(socket, reqType)
		case "3":
			register(socket)
		case "0":
			break interfaceLoop
		default:
		}
	}
	clearScreen()
}
