src = $(wildcard *.go)
target = cmd/client_linux64 cmd/client_linux32 \
	  	 cmd/client_win64 cmd/client_win32 \
	  	 cmd/client_macos

# default make all
all : $(target)

# Linux x64
cmd/client_linux64 : $(src)
	GOOS=linux GOARCH=amd64 go build -o cmd/client_linux64 *go

# Linux x32
cmd/client_linux32 : $(src)
	GOOS=linux GOARCH=386 go build -o cmd/client_linux32 *go

# Windows x64
cmd/client_win64 : $(src)
	GOOS=windows GOARCH=amd64 go build -o cmd/client_win64 *go

# Windows x32
cmd/client_win32 : $(src)
	GOOS=windows GOARCH=386 go build -o cmd/client_win32 *go

# MacOS
cmd/client_macos : $(src)
	GOOS=darwin GOARCH=amd64 go build -o cmd/client_macos *go

.PHONY : clean

clean:
	rm $(target)