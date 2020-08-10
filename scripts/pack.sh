#!/bin/bash
set -e
circleci config pack ./circleci > packed-orb.yml
