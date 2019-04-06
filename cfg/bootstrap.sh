#
# source boostrap.sh
#
# http://blog.taylormcgann.com/2012/06/13/customize-your-shell-command-prompt/
#
export PS1="\[\033[0;30;30m\]\W>\[\e[0m\] "
#
# http://osxdaily.com/2012/02/21/add-color-to-the-terminal-in-mac-os-x/
#
export CLICOLOR=1
export LSCOLORS=ExFxBxDxCxegedabagacad
#
# Global Environmental Variables
#
export JAVA_8_HOME=$(/usr/libexec/java_home -v1.8)
export JAVA_11_HOME=$(/usr/libexec/java_home -v11)

alias jdk8='export JAVA_HOME=$JAVA_8_HOME'
alias jdk11='export JAVA_HOME=$JAVA_11_HOME'
#
#export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-11.0.1.jdk/Contents/Home
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home
#
# GitHub Environmental Variables
#
export GH_HOME=/Users/acole/GitHub
export GHR_HOME=$GH_HOME/redis
export GH_CFG=$GHR_HOME/cfg
export GH_DEV_ENV=$GH_HOME/common-shared/dev-env
export GH_DEV_PKG=$GH_HOME/common-shared/dev-pkg
export GH_DEV_TOOL=$GH_HOME/common-shared/dev-tool
export GH_MAVEN_REP=$GH_HOME/common-shared/maven/.m2/repository
#
export M2_HOME=$GH_DEV_TOOL/apache-maven-3.5.4
export PATH=$PATH:$M2_HOME/bin
#
# Development Environment Variables
#
export REDIS_HOME=$GH_DEV_ENV/redis/latest
#
# Project Specific Environmental Variables
#
export APL=$GHR_HOME/apl
export APLSRC=$APL/src
export APLBIN=$APL/bin
export APLJAR=$APL/jar
export APLKIT=$APL/kit
export APLDOC=$APL/doc
#
export PATH=$PATH:$APLBIN
#
# Development Aliases
#
alias dghome='cd $GH_HOME;clear;pwd'
alias dgrhome='cd $GHR_HOME;clear;pwd'
alias dcfg='cd $GHR_HOME/cfg;clear;pwd'
alias daplsrc='cd $APLSRC;clear;pwd'
alias djava='cd $JAVA_HOME;clear;pwd'
alias dmaven='cd $GH_MAVEN_REP;clear;pwd'
alias dredis='cd $APL/src/redis;clear;pwd'
#
#alias dpython='source ~/Virtualenvs/search-py3/bin/activate'
#
alias bjarall='mvn compile source:jar javadoc:jar install'
alias bsingle='mvn assembly:single'
#
echo Redis development environment has been initialized.
daplsrc
