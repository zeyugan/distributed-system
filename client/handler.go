package main

import (
	"bytes"
	"encoding/binary"
	"fmt"
	"net"
	"runtime"
)

type Request struct {
	operation byte   // 1 byte
	uuid      string // 8 bytes
	offset    int32  // 4 bytes
	length    int32  // 4 bytes as read length when reading or monitor interval length when registering
	content   string // variable size
}

// marshal and send request to server
func request(socket *net.UDPConn, request *Request) (respCode int32, respMsg string) {
	// marshal request data
	sendData := bytes.NewBuffer([]byte{})
	binary.Write(sendData, binary.LittleEndian, request.operation)
	binary.Write(sendData, binary.LittleEndian, []byte(request.uuid))
	binary.Write(sendData, binary.LittleEndian, request.offset)
	binary.Write(sendData, binary.LittleEndian, request.length)
	binary.Write(sendData, binary.LittleEndian, []byte(request.content))

	// send data
	if debug {
		fmt.Println()
		fmt.Println("### debug msg")
		fmt.Println("### funtion:", printCallerName())
		fmt.Println("### request bytes:", sendData.Bytes())
		fmt.Println()
	}
	_, err := socket.Write(sendData.Bytes())
	if err != nil {
		fmt.Println("send data fail, err:", err)
		return
	}

	// get resp
	respCode, respMsg = recv(socket)

	return respCode, respMsg
}

// recv data for server
func recv(socket *net.UDPConn) (respCode int32, respMsg string) {

	respData := make([]byte, 4096)
	n, _, err := socket.ReadFromUDP(respData)
	if err != nil {
		fmt.Println("recv data fail, err:", err)
		return
	}

	if debug {
		fmt.Println()
		fmt.Println("### debug msg")
		fmt.Println("### caller:", printCallerName())
		fmt.Println("### resp bytes:", respData)
		fmt.Println()
	}

	respCode, respMsg = resolveResp(respData[:n])

	return respCode, respMsg
}

// resolve response
func resolveResp(resp []byte) (respCode int32, respMsg string) {
	binary.Read(bytes.NewReader(resp), binary.LittleEndian, &respCode) // unmarshal resp code
	respMsg = string(resp[4:])                                         // unmarshal resp msg

	return respCode, respMsg
}

// for debug
func printCallerName() string {
	pc, _, _, _ := runtime.Caller(2)
	return runtime.FuncForPC(pc).Name()
}
