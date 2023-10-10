src = $(wildcard *.go)

# default make all
all : executable/client_linux64 executable/client_linux32 \
	  executable/client_win64 executable/client_win32 \
	  executable/client_macos

# Linux x64
executable/client_linux64 : $(src)
	GOOS=linux GOARCH=amd64 go build -o executable/client_linux64 *go

# Linux x32
executable/client_linux32 : $(src)
	GOOS=linux GOARCH=386 go build -o executable/client_linux32 *go

# Windows x64
executable/client_win64 : $(src)
	GOOS=windows GOARCH=amd64 go build -o executable/client_win64 *go

# Windows x32
executable/client_win32 : $(src)
	GOOS=windows GOARCH=386 go build -o executable/client_win32 *go

# MacOS
executable/client_macos : $(src)
	GOOS=darwin GOARCH=amd64 go build -o executable/client_macos *go

.PHONY : clean

clean:
	rm executable/*