package main

import (
	"fmt"
	"net"
)

func main() {
	// Define the UDP address to listen on
	addr := "0.0.0.0:6666"

	// Resolve the UDP address
	udpAddr, err := net.ResolveUDPAddr("udp", addr)
	if err != nil {
		fmt.Println("Error resolving UDP address:", err)
		return
	}

	// Create a UDP connection
	conn, err := net.ListenUDP("udp", udpAddr)
	if err != nil {
		fmt.Println("Error creating UDP connection:", err)
		return
	}
	defer conn.Close()

	fmt.Printf("UDP server is listening on %s...\n", addr)

	buffer := make([]byte, 1024)

	for {
		n, addr, err := conn.ReadFromUDP(buffer)
		if err != nil {
			fmt.Println("Error reading from UDP connection:", err)
			return
		}

		// Handle the received data
		fmt.Printf("Received %d bytes from %s: %s\n", n, addr, string(buffer[:n]))

		// Respond with bytes (you can customize this response as needed)
		response := []byte("Hello, client!")

		// Send the response back to the client
		_, err = conn.WriteToUDP(response, addr)
		if err != nil {
			fmt.Println("Error sending response:", err)
			return
		}

		response2 := []byte("    update")
		_, err = conn.WriteToUDP(response2, addr)
		if err != nil {
			fmt.Println("Error sending response:", err)
			return
		}
	}
}
