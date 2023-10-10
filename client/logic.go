package main

import (
	"context"
	"fmt"
	"net"
	"strconv"
	"time"
)

type CacheStruct struct {
	content    string
	tValidated time.Time // Tc in lecture slides
	tModified  time.Time // Tmxlient in lecture slides
}

// read content from server
func readContent(socket *net.UDPConn, cache map[string]CacheStruct, freshnessInterval int) {
	filePath := ""
	offset := 0
	readLength := 0
	content := ""

	fmt.Println()
	fmt.Println("[You are reading content]")
	fmt.Printf("File path: ")
	fmt.Scanln(&filePath)
	fmt.Printf("Offset: ")
	fmt.Scanln(&offset)
	fmt.Printf("Read length: ")
	fmt.Scanln(&readLength)
	fmt.Println()

	// try to obtain content in cache
	contentCache, ok := checkCache(socket, cache, freshnessInterval, filePath)
	if ok {
		// cache is valid
		content = contentCache

	} else {
		// cache is invalid, get content from server
		respCode, respMsg := request(socket, &Request{
			operation: 'R',
			uuid:      "",
			offset:    int32(offset),
			length:    int32(readLength),
			content:   filePath,
		})

		// check file existance
		if respCode != 0 {
			fmt.Println()
			fmt.Printf("File %v doesn't exist\n", content)
			fmt.Scanln()
			return

		} else {
			content = respMsg
			serverModifiedTime := getServerModifiedTime(socket, filePath)

			// set new cache
			cache[filePath] = CacheStruct{
				content:    content,
				tValidated: time.Now(),
				tModified:  time.Unix(serverModifiedTime, 0),
			}
		}
	}

	fmt.Println()
	fmt.Println("Content: ", content)
	fmt.Scanln()

}

// check client local cache
func checkCache(socket *net.UDPConn, cache map[string]CacheStruct, freshnessInterval int, filePath string) (content string, ok bool) {
	contentCache, ok := cache[filePath]

	if ok {
		// found in cache
		if time.Now().Unix()-contentCache.tValidated.Unix() < int64(freshnessInterval) {
			// fresh and valid
			fmt.Println("* Fresh valid cache found")
			content = contentCache.content

		} else {
			fmt.Println("* Local cache is not fresh, getting modified time from server")
			// get server modified time
			serverModifiedTime := getServerModifiedTime(socket, filePath)

			if serverModifiedTime == contentCache.tModified.Unix() {
				// not fresh but valid
				fmt.Println("* Local cache is validated")
				content = contentCache.content
				// update tValidated
				cache[filePath] = CacheStruct{
					content:    contentCache.content,
					tValidated: time.Now(),
					tModified:  contentCache.tModified,
				}

			} else {
				// neither fresh nor valid
				// get latest version from server
				ok = false
			}
		}

	} else {
		// not found in cache
		ok = false
	}

	return content, ok
}

// get server modified time of a file
func getServerModifiedTime(socket *net.UDPConn, filePath string) (serverModifiedTime int64) {
	_, respMsg := request(socket, &Request{
		operation: 'T',
		uuid:      "",
		offset:    0,
		length:    0,
		content:   filePath,
	})
	serverModifiedTime, _ = strconv.ParseInt(respMsg, 10, 64)

	return serverModifiedTime
}

// insert content to a file from server
func insertContent(socket *net.UDPConn, reqType string) {
	uuid := ""
	filePath := ""
	offset := 0
	insertion := ""

	fmt.Println()
	fmt.Println("[You are interting content]")

	// use uuid to identify req in order to make sure idempotence
	if reqType == "idempotence" {
		uuid = getUUID(socket)
		fmt.Println("You are using idempotent type, UUID for this request is:", uuid)
	}

	fmt.Printf("File path: ")
	fmt.Scanln(&filePath)
	fmt.Printf("Offset: ")
	fmt.Scanln(&offset)
	fmt.Printf("Insertion: ")
	fmt.Scanln(&insertion)

	_, respMsg := request(socket, &Request{
		operation: 'W',
		uuid:      uuid,
		offset:    int32(offset),
		length:    0,
		content:   filePath + "|" + insertion,
	})

	fmt.Println()
	fmt.Println("Server resp:", respMsg)
	fmt.Scanln()
}

// register(subscribe) to a file's update from server
func register(socket *net.UDPConn) {
	filePath := ""
	monitorInterval := 0 // s

	fmt.Println()
	fmt.Printf("File path: ")
	fmt.Scanln(&filePath)
	fmt.Printf("Monitor interval (s): ")
	fmt.Scanln(&monitorInterval)

	respCode, respMsg := request(socket, &Request{
		operation: 'S',
		uuid:      "",
		offset:    0,
		length:    int32(monitorInterval * 1000),
		content:   filePath,
	})

	if respCode != 0 {
		fmt.Println(respMsg)
	}

	waitCtx, cancelWaitCtx := context.WithCancel(context.Background())

	// create routine to listen to server for update msg
	// routine can be ended by ctx
	go func(waitCtx context.Context) {
		for {
			select {
			// end routine when timeout
			case <-waitCtx.Done():
				return
			default:
				_, respMsg := recv(socket)
				fileUpdateMsg := respMsg
				fmt.Println("The file you subscribe is updated to :", fileUpdateMsg)
			}
		}
	}(waitCtx)

	// use channel to block the process to achieve waiting
	// when timeout, manually end the routine with ctx
	timeout := time.After(time.Duration(monitorInterval) * time.Second)
	<-timeout
	cancelWaitCtx()

	fmt.Println("Time's up, monioring ends")
	fmt.Scan()

}

// get uuid for at-most-once request
func getUUID(socket *net.UDPConn) (uuid string) {
	_, respMsg := request(socket, &Request{
		operation: 'I',
		uuid:      "",
		offset:    0,
		length:    0,
		content:   "",
	})

	uuid = respMsg

	return uuid
}
