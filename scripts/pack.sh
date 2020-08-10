#!/bin/bash
set -e
circleci config pack ../scripts/src > ../scripts/packed-orb.yml
