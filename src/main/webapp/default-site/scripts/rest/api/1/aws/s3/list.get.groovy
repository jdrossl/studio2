def s3Service = applicationContext["studioS3Service"]

return s3Service.listFiles(params.site, params.profile)