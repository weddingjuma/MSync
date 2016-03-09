#!/usr/bin/env python
# -*- coding: utf-8 -*-
# vim: ai ts=4 sts=4 et sw=4 nu

from __future__ import (unicode_literals, absolute_import,
                        division, print_function)
import logging
import sys
import os
import subprocess
import shutil

CURRENT_DIR = os.path.dirname(os.path.abspath(__file__))
CERTIFICATE = None
STOREPASSWD = ''
KEYPASSWD = ''

logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)


def apply_to(fpath, func):
    content = ''
    with open(fpath, 'r') as f:
        content = f.read()
    content = func(content)
    with open(fpath, 'w') as f:
        f.write(content)


def build_client():

    os.chdir('client')

    if CERTIFICATE is not None:
        build_fpath = os.path.join('app', 'build.gradle')
        build_orig_fpath = os.path.join('app', 'build.gradle.orig')
        shutil.copy2(build_fpath, build_orig_fpath)
        apply_to(build_fpath,
                 lambda x: x.replace('// SIGNING',
                                     'signingConfig signingConfigs.release')
                 .replace('CERTIFICATE', CERTIFICATE))

    # build APK
    subprocess.call('rm -f app/build/outputs/apk/*.apk', shell=True)
    subprocess.call(['./gradlew', 'assembleRelease'])

    if CERTIFICATE is not None:
        # restore build.gradle if required
        if os.path.isfile(build_orig_fpath):
            shutil.move(build_orig_fpath, build_fpath)

    # copy signed version to proper location
    logger.info("copying final file")
    target_fname = 'app-release.apk' if CERTIFICATE \
        else 'app-release-unsigned.apk'
    final_fname = 'fondasms-client{}.apk'.format(
        '' if CERTIFICATE else '-unsigned')
    shutil.copy2(os.path.join('app', 'build', 'outputs', 'apk', target_fname),
                 os.path.join(CURRENT_DIR, 'store', 'apks', final_fname))

    # go back
    os.chdir(CURRENT_DIR)
    subprocess.call(['ls', '-alh', os.path.join('store', 'apks')])

if __name__ == '__main__':

    if len(sys.argv) < 2:
        logger.error("Must set certificate path (or --no-signing)")
        sys.exit(1)

    if os.path.isfile(sys.argv[1]):
        CERTIFICATE = os.path.expanduser(sys.argv[1])
    else:
        CERTIFICATE = None

    if CERTIFICATE is not None:
        for key in ('KSTOREPWD', 'KEYPWD'):
            if key not in os.environ:
                logger.error("{} not found in environ. signing will fail."
                             .format(key))
                sys.exit(1)

    os.chdir(CURRENT_DIR)
    logger.info("Building client")
    build_client()
