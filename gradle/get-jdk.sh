#!/bin/bash

if [ -z "$CI" ]; then

  # OS specific support (must be 'true' or 'false').
  windows=false
  darwin=false
  linux=false
  case "`uname`" in
    CYGWIN* )
      windows=true
      ;;
    Darwin* )
      darwin=true
      ;;
    MINGW* )
      windows=true
      ;;
    MSYS* )
      windows=true
      ;;
    Linux* )
      linux=true
      ;;
  esac

  GRADLE_HOME="${GRADLE_USER_HOME:-${HOME}/.gradle}"

  if [ ! -z "$USERPROFILE" ]; then
    # msys
    USERPROFILE_CYG=$(cygpath $USERPROFILE)
    GRADLE_HOME="${USERPROFILE_CYG}/.gradle"
  fi

  OPENJDK_DIR="$GRADLE_HOME/curiostack/openjdk"

  export JAVA_HOME="$OPENJDK_DIR/jdk-11.0.2"

  if "$linux" = "true"; then
    SRC="https://download.java.net/java/GA/jdk11/9/GPL/openjdk-11.0.2_linux-x64_bin.tar.gz"
    DEST="$OPENJDK_DIR/openjdk-11.0.2_linux-x64_bin.tar.gz"
  fi

  if "$darwin" = "true"; then
    SRC="https://download.java.net/java/GA/jdk11/9/GPL/openjdk-11.0.2_osx-x64_bin.tar.gz"
    DEST="$OPENJDK_DIR/openjdk-11.0.2_osx-x64_bin.tar.gz"
  fi

  if "$windows" = "true"; then
    SRC="https://download.java.net/java/GA/jdk11/9/GPL/openjdk-11.0.2_windows-x64_bin.zip"
    DEST="$OPENJDK_DIR/openjdk-11.0.2_windows-x64_bin.zip"
  fi

  if [ ! -d "$JAVA_HOME" ]; then
    mkdir -p "$OPENJDK_DIR"

    echo "Downloading OpenJDK"
    curl "$SRC" -o "$DEST"

    cd "$OPENJDK_DIR"

    if "$windows" = "true"; then
      unzip "$DEST"
    else
      tar -xf "$DEST"
    fi
  fi

fi
