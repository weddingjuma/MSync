#!/usr/bin/env python
# -*- coding: utf-8 -*-
# vim: ai ts=4 sts=4 et sw=4 nu

from __future__ import (unicode_literals, absolute_import,
                        division, print_function)
import logging
import subprocess
import shutil
import os
import tempfile
import sys

CURRENT_DIR = os.path.dirname(os.path.abspath(__file__))
CERTIFICATE = '--no-signing'
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


def build_pack(pid, pident):

    # copy source tree to temp directory
    new_path = tempfile.mkdtemp()
    new_root = os.path.join(new_path, pident)
    shutil.copytree(CURRENT_DIR, new_root)

    # move to new root
    os.chdir(new_root)
    logger.debug(new_root)

    # Manifest
    manifest_fpath = os.path.join('app', 'src', 'main', 'AndroidManifest.xml')
    apply_to(manifest_fpath, lambda x: x.replace('pack01', pident)
                                        .replace('android:priority="1"',
                                                 'android:priority="{}"'
                                                 .format(pid)))

    # rename package
    source_root = os.path.join('app', 'src', 'main', 'java', 'com',
                               'yeleman', 'fondasms')
    new_java_source = os.path.join(source_root, pident)
    shutil.move(os.path.join(source_root, 'pack01'),
                new_java_source)

    # rename package in all java files
    for fname in os.listdir(new_java_source):
        apply_to(os.path.join(new_java_source, fname),
                 lambda x: x.replace('pack01', pident))

    # rename app_name in strings
    apply_to(os.path.join('app', 'src', 'main', 'res',
                          'values', 'strings.xml'),
             lambda x: x.replace('Pack 1', "Pack {}".format(pid)))

    # rename appId in build.gradle
    apply_to(os.path.join('app', 'build.gradle'),
             lambda x: x.replace('pack01', pident))

    if CERTIFICATE is not None:
        apply_to(os.path.join('app', 'build.gradle'),
                 lambda x: x.replace('// SIGNING',
                                     'signingConfig signingConfigs.release')
                            .replace('CERTIFICATE', CERTIFICATE))

    # build APK
    subprocess.call('rm -f build/outputs/apk/*.apk', shell=True)
    subprocess.call(['./gradlew', 'build', 'assembleRelease'])

    # copy signed version to proper location
    logger.info("copying final file")
    target_fname = 'app-release.apk' if CERTIFICATE \
        else 'app-release-unsigned.apk'
    final_fname = 'fondasms-{}{}.apk'.format(
        pident, '' if CERTIFICATE else '-unsigned')
    shutil.copy2(os.path.join('app', 'build', 'outputs', 'apk', target_fname),
                 os.path.join(CURRENT_DIR, 'store', 'apks', final_fname))

    # go back
    os.chdir(CURRENT_DIR)
    subprocess.call(['ls', '-alh', os.path.join('store', 'apks')])

    # remove temp folder
    shutil.rmtree(new_path)

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
    nb_packs = 1
    parent = os.path.dirname(CURRENT_DIR)
    with open(os.path.join(parent, 'client', 'app', 'src', 'main', 'res',
                           'xml', 'expansion_packs.xml'), 'r') as f:
        nb_packs = sum([1 for line in f.readlines()
                        if 'android:key="pack' in line])

    for pid in range(1, nb_packs + 1):
        pident = "pack{}".format(str(pid).zfill(2))
        logger.info("Building {}".format(pident))
        build_pack(pid, pident)
