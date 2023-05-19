import React from 'react';
import {
    Nav,
    NavLink,
    NavMenu,
    NavBtn,
    NavBtnLink
} from './NavbarElements';
import { useNavigate } from "react-router-dom";

const Navbar = () => {
    let isUserLoggedIn = sessionStorage.getItem('token');
    const navigate = useNavigate();

    const handleLogInButton = event => {
        navigate('/login');
    }

    const handleLogOutButton = event => {
        navigate('');
        sessionStorage.clear();
    }

    return (
        <>
            <Nav>
                <NavLink to="">
                    <h3>Digi Brain</h3>
                </NavLink>
                <NavMenu>
                    {(isUserLoggedIn !== null) &&
                        <NavLink to="/learn" >
                            Learn
                        </NavLink>
                    }

                    {(isUserLoggedIn !== null) &&
                        <NavLink to="/quiz" >
                            Quiz
                        </NavLink>
                    }

                    {(isUserLoggedIn !== null) &&
                        <NavLink to="/learn-path" >
                            Learn Path
                        </NavLink>
                    }

                    {(isUserLoggedIn !== null) &&
                        <NavLink to="/profile" >
                            Profile
                        </NavLink>
                    }
                </NavMenu>
                {(isUserLoggedIn === null) ?
                    <NavBtn>
                        <NavBtnLink onClick={handleLogInButton}>Sign In</NavBtnLink>
                    </NavBtn> :
                    <NavBtn>
                        <NavBtnLink onClick={handleLogOutButton}>Sign Out</NavBtnLink>
                    </NavBtn>
                }
            </Nav>
        </>
    );
};

export default Navbar;