# Super SKY-lAB 

## Builing 

### Buildah (Recommended)

#### Installing `Buildah`

##### [Nix (Recommended)](https://nixos.org/download/)

First install `Nix` package manager

###### MacOS

```bash
sh <(curl --proto '=https' --tlsv1.2 -L https://nixos.org/nix/install)
```

###### Linux 

```bash 
sh <(curl --proto '=https' --tlsv1.2 -L https://nixos.org/nix/install) --daemon
```
--- 
Installing `buildah ` with nix 
Temporarily build environment:
```bash
nix-shell -p buildah
```
Persistent build environment:
```bash
nix-env -iA nixpkgs.buildah
```

##### [Non-Nix Installation on Linux Distros](https://github.com/containers/buildah/blob/main/install.md)
- https://github.com/containers/buildah/blob/main/install.md

--- 

General usage:
```bash
buildah bud -f Dockerfile --jobs "$(nproc)" -t super-skylab-app:latest .
```

Specific tag:
```bash
buildah bud -f Dockerfile --jobs "$(nproc)" -t super-skylab-app:stable .
```

Modified core count:
```bash
buildah bud -f Dockerfile --jobs 6 -t super-skylab-app:stable .
```

Building with different `Containerfile` (`Dockerfile`):
```bash
buildah bud -f Dockerfile-dev --jobs "$(nproc)" -t super-skylab-app:stable .
```


### Docker 

#### Installing `Docker`

```bash
curl https://get.docker.com | bash
```

--- 

```bash 
docker build -t super-skylab-app:latest .
```

### MacOS Specific Build Parameters
This setting enables images to run on Linux servers with the `x86_64` architecture. Otherwise, the images you compile will not run on the servers.
```bash 
buildah bud --SNIP-- --platform linux/amd64 -t super-skylab-app:tag_name .
```

or 

```bash 
docker build --platform linux/amd64 -t super-skylab-app:tag_name .
```
