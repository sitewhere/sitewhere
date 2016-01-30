#!/bin/bash

if [ "$TRAVIS_REPO_SLUG" == "sitewhere/sitewhere" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "sitewhere-1.6.0" ]; then

  echo -e "Publishing javadoc...\n"

  cp -R sitewhere-core-api/build/docs/javadoc $HOME/sitewhere-core-api
  cp -R sitewhere-server-api/build/docs/javadoc $HOME/sitewhere-server-api

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet https://${GH_TOKEN}@github.com/sitewhere/sitewhere.github.io sitewhere.github.io > /dev/null

  cd sitewhere.github.io
  git rm -rf ./javadoc
  mkdir javadoc
  cp -Rf $HOME/sitewhere-core-api ./javadoc/sitewhere-core-api
  cp -Rf $HOME/sitewhere-server-api ./javadoc/sitewhere-server-api
  git add -f .
  git commit -m "Latest javadoc on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to sitewhere.github.io."
  git push -fq origin master > /dev/null

  echo -e "Published Javadoc to sitewhere.github.io.\n"
  
fi