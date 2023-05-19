import { NavLink as Link } from 'react-router-dom';
import styled from 'styled-components';

export const Nav = styled.nav`
  background: #005e78;
  height: 70px;
  display: flex;
  justify-content: flex-start;
  padding-left: 100px;
  padding-right: 100px;
`;

export const NavLink = styled(Link)`
  color: #fff;
  display: flex;
  align-items: center;
  font-size: 20px;
  text-decoration: none;
  padding: 0 1rem;
  height: 100%;
  cursor: pointer;
  &.active {
    color: #deeb34;
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
  display: flex;
  align-items: center;
  margin-left: auto;
`;

export const NavBtnLink = styled.nav`
  border-radius: 4px;
  background: #fff;
  padding: 10px 22px;
  font-size: 20px;
  color: #010606;
  outline: none;
  border: none;
  cursor: pointer;
  &:hover {
    transition: all 0.2s ease-in-out;
    background: #deeb34;
    color: #005e78;
  }
`;