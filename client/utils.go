package main

import (
	"os"
	"os/exec"
)

// clear the screen
func clearScreen() {
	cmd := exec.Command("clear")
	cmd.Stdout = os.Stdout
	cmd.Run()
}
