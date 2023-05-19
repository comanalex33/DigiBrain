import React, { useState, useEffect } from 'react';
import AWS from 'aws-sdk';

import '../App.css'

import apiService from '../services/ApiService';

function Profile() {

    const [user, setUser] = useState()
    const [imageUrl, setImageUrl] = useState('');

    const token = sessionStorage.getItem('token')
    const username = sessionStorage.getItem('username')
    const role = sessionStorage.getItem('role')
    const config = { headers: { 'Authorization': 'Bearer ' + token } }

    useEffect(() => {
        apiService.getUserDetails(username, config)
            .then(async response => {
                setUser(response.data)
                getProfileImage(response.data.profileImageName)
            })
            .catch(error => {
                console.log(error)
            })
    }, []);

    const getProfileImage = (imageName) => {
        apiService.getObjectStorageInfo(config)
            .then(response => {
                AWS.config.update({
                    accessKeyId: response.data.readAccessKey,
                    secretAccessKey: response.data.readSecretKey,
                    region: response.data.bucketRegion
                })

                const s3 = new AWS.S3({
                    params: {
                        Bucket: response.data.bucketName
                    }
                })

                s3.getObject({
                    Key: imageName
                }, function (err, data) {
                    if (err) {
                        console.log(err);
                        return;
                    }
                    const image = data.Body.toString('base64')
                    setImageUrl(`data:image/jpeg;base64,${image}`);

                    console.log("Image downloaded");
                    // Use the downloaded data as needed
                });
            })
            .catch(error => {
                console.log(error)
            })
    }

    return (
        <div className='Page-fit'>
            <div className='d-flex h-100'>
                <div className="flex-grow-1 d-flex justify-content-center align-items-center" style={{ width: 40 + '%' }}>
                    {imageUrl && <img src={imageUrl} alt="Your image" className='Home-image' />}
                </div>
                <div className="flex-grow-1" style={{ width: 60 + '%' }}>
                    <div className='Home-title'>Administration Page</div>
                    <div className='Home-content'>
                        {username}
                    </div>
                    <div className='Home-footer'><i>Explore, Learn, Succeed: Your Educational Journey Starts Here</i></div>
                </div>
            </div>
        </div>
    );
};

export default Profile;
