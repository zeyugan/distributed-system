package main

import (
	"strconv"
	"testing"
)

func TestResolveResp(t *testing.T) {
	// respCode = 0, respMsg = "Hello"
	testBytes := []byte{48, 117, 0, 0, 72, 101, 108, 108, 111}

	respCode, respMsg := resolveResp(testBytes)

	t.Logf(strconv.Itoa(int(respCode)))
	t.Logf(respMsg)
}
