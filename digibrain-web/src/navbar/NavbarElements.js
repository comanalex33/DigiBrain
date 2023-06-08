import { NavLink as Link } from 'react-router-dom';
import styled from 'styled-components';

export const Nav = styled.nav`
  height: 70px;
  display: flex;
  justify-content: flex-start;
  padding-left: 100px;
  padding-right: 100px;
`;

export const NavLink = styled(Link)`
  color: #3F456C;
  display: flex;
  align-items: center;
  font-size: 20px;
  text-decoration: none;
  padding: 0 1rem;
  height: 100%;
  cursor: pointer;
  &.active {
    color: #7adcff;
  }
  &:hover {
    color: #7adcff;
  }
`;

export const NavMenu = styled.div`
  display: flex;
  align-items: center;
`;

export const NavBtn = styled.nav`
  color: #005e78;
  display: flex;
  align-items: center;
  margin-left: auto;
`;

export const NavBtnLink = styled.nav`
  border-radius: 4px;
  background: #3F456C;
  padding: 10px 22px;
  font-size: 20px;
  color: #fff;
  outline: none;
  border: none;
  cursor: pointer;
  &:hover {
    transition: all 0.2s ease-in-out;
    background: #deeb34;
    color: #005e78;
  }
`;