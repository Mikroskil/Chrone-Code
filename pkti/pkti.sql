-- phpMyAdmin SQL Dump
-- version 2.10.1
-- http://www.phpmyadmin.net
-- 
-- Host: localhost
-- Generation Time: Nov 27, 2013 at 06:18 AM
-- Server version: 5.0.41
-- PHP Version: 5.2.2

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

-- 
-- Database: `pkti`
-- 

-- --------------------------------------------------------

-- 
-- Table structure for table `register`
-- 

CREATE TABLE `register` (
  `username` varchar(50) collate latin1_general_ci NOT NULL,
  `password` varchar(50) collate latin1_general_ci NOT NULL,
  `confirm` varchar(50) collate latin1_general_ci NOT NULL,
  `email` varchar(50) collate latin1_general_ci NOT NULL,
  `event_name` varchar(100) collate latin1_general_ci NOT NULL,
  `event_date` tinyint(2) NOT NULL,
  `event_month` varchar(10) collate latin1_general_ci NOT NULL,
  `event_year` tinyint(4) NOT NULL,
  `event_desc` varchar(500) collate latin1_general_ci NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_general_ci;

-- 
-- Dumping data for table `register`
-- 

