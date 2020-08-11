#!/bin/bash
set -e
circleci config pack scripts/src > packed-orb.yml
