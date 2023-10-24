package main

import (
	"fmt"
	"net"
)

// copy content to a another file
func copyContentToAnotherFile(socket *net.UDPConn, reqType string) {
	uuid := defaultUUID
	sourceFilePath := ""
	offset := 0
	copyLength := 0
	targetFilePath := ""

	fmt.Println()
	fmt.Println("[You are copying content to another file]")

	// use uuid to identify req in order to make sure idempotence
	if reqType == "idempotence" {
		uuid = getUUID(socket)
		fmt.Println("You are using idempotent type, UUID for this request is:", uuid)
	}

	fmt.Printf("Source file path: ")
	fmt.Scanln(&sourceFilePath)
	fmt.Printf("Offset: ")
	fmt.Scanln(&offset)
	fmt.Printf("Copy length: ")
	fmt.Scanln(&copyLength)
	fmt.Printf("Target file path: ")
	fmt.Scanln(&targetFilePath)

	respCode, respMsg := request(socket, &Request{
		operation: 'C',
		uuid:      uuid,
		offset:    int32(offset),
		length:    int32(copyLength),
		content:   sourceFilePath + "|" + targetFilePath,
	})

	if respCode != 0 {
		fmt.Println("File is not exist")
		fmt.Println()
		fmt.Printf("Please press enter to continue...")
		fmt.Scanln()
		return
	}

	fmt.Println()
	fmt.Println("Server resp:", respMsg)
	fmt.Printf("Please press enter to continue...")
	fmt.Scanln()
}
